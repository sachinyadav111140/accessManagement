package com.springSecurity.accessManagement.services;

import com.springSecurity.accessManagement.models.entities.Permission;
import com.springSecurity.accessManagement.repositories.PermissionRepository;

import com.springSecurity.accessManagement.services.interfaces.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
   private PermissionRepository permissionRepository;

    @Override
    public List<Permission> findAll() {
        List<Permission> list = new ArrayList<>();
        permissionRepository.findAll().iterator().forEachRemaining(list::add);

        return list;
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    public Optional<Permission> findById(String id) {
        return permissionRepository.findById(Long.parseLong(id));
    }
}
