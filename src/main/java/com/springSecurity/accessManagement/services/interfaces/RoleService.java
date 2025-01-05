package com.springSecurity.accessManagement.services.interfaces;



import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.dtos.CreateRoleDto;
import com.springSecurity.accessManagement.models.entities.Role;

import java.util.List;

public interface RoleService {
    Role save(CreateRoleDto role);

    List<Role> findAll();

    void delete(String id);

    Role findByName(String name) throws ResourceNotFoundException;

    Role findById(String id) throws ResourceNotFoundException;

    Role update(String id, CreateRoleDto createRoleDto) throws ResourceNotFoundException;
    Role update(Role role);
}
