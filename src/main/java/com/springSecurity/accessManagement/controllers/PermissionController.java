package com.springSecurity.accessManagement.controllers;

import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.entities.Permission;
import com.springSecurity.accessManagement.models.response.*;
import com.springSecurity.accessManagement.services.interfaces.PermissionService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.springSecurity.accessManagement.constants.Constants.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

@Tag(name = SWG_PERMISSION_TAG_NAME, description = SWG_PERMISSION_TAG_DESCRIPTION)
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/admin/permissions")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    @Operation(
            summary = SWG_PERMISSION_LIST_OPERATION,
            description = SWG_PERMISSION_LIST_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_PERMISSION_LIST_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleListResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class)))
            }
    )
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @GetMapping
    public ResponseEntity<PermissionListResponse> all() {
        return ResponseEntity.ok(new PermissionListResponse(permissionService.findAll()));
    }

    @Operation(
            summary = SWG_PERMISSION_ITEM_OPERATION,
            description = SWG_PERMISSION_ITEM_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_PERMISSION_ITEM_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = RoleResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "404", description = PERMISSION_NOT_FOUND_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class)))
            }
    )
    @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<PermissionResponse> one(@PathVariable String id)
            throws ResourceNotFoundException {
        Optional<Permission> permission = permissionService.findById(id);

        if (permission.isEmpty()) {
            throw new ResourceNotFoundException(PERMISSION_NOT_FOUND_MESSAGE);
        }

        return ResponseEntity.ok(new PermissionResponse(permission.get()));
    }
}
