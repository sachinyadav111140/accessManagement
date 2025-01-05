package com.springSecurity.accessManagement.services;


import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.dtos.CreateRoleDto;
import com.springSecurity.accessManagement.models.entities.Role;
import com.springSecurity.accessManagement.repositories.RoleRepository;
import com.springSecurity.accessManagement.services.interfaces.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static com.springSecurity.accessManagement.constants.Constants.ROLE_NOT_FOUND_MESSAGE;


@Service(value = "roleService")
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role save(CreateRoleDto createRoleDto) {
        return roleRepository.save(createRoleDto.toRole());
    }

    @Override
    public List<Role> findAll() {
        List<Role> list = new ArrayList<>();
        roleRepository.findAll().iterator().forEachRemaining(list::add);

        return list;
    }

    @Override
    public void delete(String id) {
        roleRepository.deleteById(Long.parseLong(id));
    }

    @Override
    public Role findByName(String name) throws ResourceNotFoundException {
        Optional<Role> roleOptional = roleRepository.findByName(name);

        if (roleOptional.isEmpty()) {
            throw new ResourceNotFoundException(ROLE_NOT_FOUND_MESSAGE);
        }

        return roleOptional.get();
    }

    @Override
    public Role findById(String id) throws ResourceNotFoundException {
        Optional<Role> roleOptional = roleRepository.findById(Long.parseLong(id));

        if (roleOptional.isEmpty()) {
            throw new ResourceNotFoundException(ROLE_NOT_FOUND_MESSAGE);
        }

        return roleOptional.get();
    }

    @Override
    public Role update(String id, CreateRoleDto createRoleDto) throws ResourceNotFoundException {
        Role roleToUpdate = findById(id);

        roleToUpdate
            .setName(createRoleDto.getName())
            .setDescription(createRoleDto.getDescription());

        return roleRepository.save(roleToUpdate);
    }

    @Override
    public Role update(Role role) {
        return roleRepository.save(role);
    }
}
