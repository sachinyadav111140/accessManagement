package com.springSecurity.accessManagement.services;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.springSecurity.accessManagement.models.dtos.PermissionLoadDto;
import com.springSecurity.accessManagement.models.entities.Permission;
import com.springSecurity.accessManagement.models.entities.Role;
import com.springSecurity.accessManagement.models.enums.PermissionLoadMode;
import com.springSecurity.accessManagement.repositories.PermissionRepository;
import com.springSecurity.accessManagement.repositories.RoleRepository;
import com.springSecurity.accessManagement.services.interfaces.PermissionLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class PermissionLoaderImpl implements PermissionLoader {


  @Value("${app.permission.load.mode}")
  private PermissionLoadMode loadMode;

  @Autowired
  private RoleRepository roleRepository;

  @Autowired
  private PermissionRepository permissionRepository;


  private void addPermissionToRole(Permission permission, String[] roleNames) {
    Arrays.stream(roleNames).parallel().forEach(roleName -> {
      Optional<Role> role = roleRepository.findByName(roleName);

      role.ifPresent(roleFound -> {
        if (!roleFound.hasPermission(permission.getName())) {
          roleFound.addPermission(permission);

          roleRepository.save(roleFound);
        }
      });
    });
  }

  private void loadPermissions(List<PermissionLoadDto> permissionLoadDtoList) {
    permissionLoadDtoList.parallelStream().forEach(permissionLoadDto -> {
      Permission permissionCreated;
      Optional<Permission> permission = permissionRepository.findByName(permissionLoadDto.getName());

      if (permission.isEmpty()) {
        permissionCreated = permissionRepository.save(permissionLoadDto.toPermission());
      } else {
        permissionCreated = permission.get();
      }

      addPermissionToRole(permissionCreated, permissionLoadDto.getRoleNames());
    });
  }

  @Override
  public void load() {
    List<PermissionLoadDto> permissionLoadDtoList;

    if (loadMode.equals(PermissionLoadMode.CREATE)) {
      permissionRepository.deleteAll();
    }

    Resource resource = new ClassPathResource("permission.json");

    try (InputStream inputStream = resource.getInputStream()) {

      byte[] binaryData = FileCopyUtils.copyToByteArray(inputStream);
      String data = new String(binaryData, StandardCharsets.UTF_8);

      Type permissionLoadDtoListType = new TypeToken<ArrayList<PermissionLoadDto>>() {
      }.getType();

      permissionLoadDtoList = new Gson().fromJson(data, permissionLoadDtoListType);
      loadPermissions(permissionLoadDtoList);
    } catch (IOException ignored) {
      log.error("Loading permissions: failed to read permission file!");
    }
  }
}
