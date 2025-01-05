package com.springSecurity.accessManagement.controllers;

import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.dtos.CreateRoleDto;
import com.springSecurity.accessManagement.models.dtos.UpdateRolePermissionDto;
import com.springSecurity.accessManagement.models.entities.Permission;
import com.springSecurity.accessManagement.models.entities.Role;
import com.springSecurity.accessManagement.models.response.*;
import com.springSecurity.accessManagement.services.interfaces.PermissionService;
import com.springSecurity.accessManagement.services.interfaces.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Optional;

import static com.springSecurity.accessManagement.constants.Constants.*;

@Tag(name = SWG_ROLE_TAG_NAME, description = SWG_ROLE_TAG_DESCRIPTION)
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @Autowired
    private PermissionService permissionService;



    @Operation(summary = SWG_ROLE_CREATE_OPERATION, description = "Create a new role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_CREATE_MESSAGE, content = @Content(schema = @Schema(implementation = Role.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(schema = @Schema(implementation = InvalidDataResponse.class)))
    })

    @PostMapping
    @PreAuthorize("hasAuthority('create:role')")
    public ResponseEntity<Role> create(@RequestBody CreateRoleDto createRoleDto) {
        Role role = roleService.save(createRoleDto);
        return ResponseEntity.ok(role);
    }

    @Operation(summary = SWG_ROLE_LIST_OPERATION, description = "Get a list of all roles")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_LIST_MESSAGE, content = @Content(schema = @Schema(implementation = RoleListResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class)))
    })

    @GetMapping
    @PreAuthorize("hasAuthority('read:roles')")
    public ResponseEntity<RoleListResponse> all() {

        return ResponseEntity.ok(new RoleListResponse(roleService.findAll()));
    }

    @Operation(summary = SWG_ROLE_ITEM_OPERATION, description = "Get details of a specific role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_ITEM_MESSAGE, content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class)))
    })
    @PreAuthorize("hasAuthority('read:role')")
    @GetMapping("/{id}")
    public ResponseEntity<RoleResponse> one(@PathVariable String id) throws ResourceNotFoundException {
        Role role = roleService.findById(id);
        return ResponseEntity.ok(new RoleResponse(role));
    }

    @Operation(summary = SWG_ROLE_UPDATE_OPERATION, description = "Update an existing role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_UPDATE_MESSAGE, content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(schema = @Schema(implementation = InvalidDataResponse.class)))
    })

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('update:role')")
    public ResponseEntity<RoleResponse> update(@PathVariable String id, @RequestBody CreateRoleDto createRoleDto)
            throws ResourceNotFoundException {
        return ResponseEntity.ok(new RoleResponse(roleService.update(id, createRoleDto)));
    }

    @Operation(summary = SWG_ROLE_DELETE_OPERATION, description = "Delete a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = SWG_ROLE_DELETE_MESSAGE, content = @Content),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class)))
    })
    @PreAuthorize("hasAuthority('delete:role')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        roleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = SWG_ROLE_ASSIGN_PERMISSION_OPERATION, description = "Assign permissions to a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_ASSIGN_PERMISSION_MESSAGE, content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(schema = @Schema(implementation = InvalidDataResponse.class)))
    })

    @PutMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('add:permission')")
    public ResponseEntity<RoleResponse> addPermissions(@PathVariable String id, @RequestBody UpdateRolePermissionDto updateRolePermissionDto)
            throws ResourceNotFoundException {
        Role role = roleService.findById(id);

        Arrays.stream(updateRolePermissionDto.getPermissions()).forEach(permissionName -> {
            Optional<Permission> permission = permissionService.findByName(permissionName);

            if (permission.isPresent() && !role.hasPermission(permissionName)) {
                role.addPermission(permission.get());
            }
        });

        Role roleUpdated = roleService.update(role);

        return ResponseEntity.ok().body(new RoleResponse(roleUpdated));
    }

    @Operation(summary = SWG_ROLE_REMOVE_PERMISSION_OPERATION, description = "Remove permissions from a role")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = SWG_ROLE_REMOVE_PERMISSION_MESSAGE, content = @Content(schema = @Schema(implementation = RoleResponse.class))),
            @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(schema = @Schema(implementation = BadRequestResponse.class))),
            @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(schema = @Schema(implementation = InvalidDataResponse.class)))
    })
    @DeleteMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('remove:permission')")
    public ResponseEntity<RoleResponse> removePermissions(@PathVariable String id, @RequestBody UpdateRolePermissionDto updateRolePermissionDto)
            throws ResourceNotFoundException {
        Role role = roleService.findById(id);

        Arrays.stream(updateRolePermissionDto.getPermissions()).forEach(permissionName -> {
            Optional<Permission> permission = permissionService.findByName(permissionName);

            if (permission.isPresent() && role.hasPermission(permissionName)) {
                role.removePermission(permission.get());
            }
        });

        Role roleUpdated = roleService.update(role);

        return ResponseEntity.ok().body(new RoleResponse(roleUpdated));
    }
}
