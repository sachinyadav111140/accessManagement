package com.springSecurity.accessManagement.bootstrap;


import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.dtos.CreateRoleDto;
import com.springSecurity.accessManagement.models.dtos.CreateUserDto;
import com.springSecurity.accessManagement.services.interfaces.PermissionLoader;
import com.springSecurity.accessManagement.services.interfaces.RoleService;
import com.springSecurity.accessManagement.services.interfaces.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

import static com.springSecurity.accessManagement.constants.Constants.*;


@Component
public class DataSeeder implements ApplicationListener<ContextRefreshedEvent> {

    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionLoader permissionLoader;

    @Autowired
    private UserService userService;

    @SneakyThrows
    @Override
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        loadRoles();
        permissionLoader.load();
        loadUsers();


    }

    private void loadRoles() {
        Map<String, String> rolesMap = new HashMap<>();
        rolesMap.put(ROLE_USER, "User role");
        rolesMap.put(ROLE_ADMIN, "Admin role");
        rolesMap.put(ROLE_SUPER_ADMIN, "Super admin role");

        rolesMap.forEach((key, value) -> {
            try {
                roleService.findByName(key);
            }
            catch (ResourceNotFoundException e) {
                CreateRoleDto createRoleDto = new CreateRoleDto();

                createRoleDto.setName(key)
                    .setDescription(value)
                    .setDefault(true);

                roleService.save(createRoleDto);
            }
        });
    }

    private void loadUsers() throws ResourceNotFoundException {
        CreateUserDto superAdmin = new CreateUserDto()
                .setEmail("sachinyadav111140@gmail.com")
                .setFirstName("Super")
                .setLastName("Admin")
                .setConfirmed(true)
                .setEnabled(true)
                .setAvatar(null)
                .setGender("M")
                .setTimezone("Europe/Paris")
                .setCoordinates(null)
                .setPassword("secret");

        try {
            userService.findByEmail(superAdmin.getEmail());
        } catch (ResourceNotFoundException e) {
            superAdmin.setRole(roleService.findByName(ROLE_SUPER_ADMIN));

            userService.save(superAdmin);
        }
    }


}
