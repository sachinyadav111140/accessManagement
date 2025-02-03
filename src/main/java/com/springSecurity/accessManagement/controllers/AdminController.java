package com.springSecurity.accessManagement.controllers;

import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.dtos.CreateUserDto;
import com.springSecurity.accessManagement.models.entities.Role;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.models.response.BadRequestResponse;
import com.springSecurity.accessManagement.models.response.InvalidDataResponse;
import com.springSecurity.accessManagement.models.response.UserResponse;
import com.springSecurity.accessManagement.services.interfaces.RoleService;
import com.springSecurity.accessManagement.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import static com.springSecurity.accessManagement.constants.Constants.*;

@Tag(name = SWG_ADMIN_TAG_NAME, description = SWG_ADMIN_TAG_DESCRIPTION)
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/admin/admins")
@Slf4j
public class AdminController {

  @Autowired
  private RoleService roleService;

  @Autowired
  private UserService userService;

  @Operation(
          summary = "Create a new admin user",
          description = "Creates a new admin user with the specified details.",
          tags = {"Admin Management"}
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Admin user created successfully.",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid input data.",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))
          ),
          @ApiResponse(
                  responseCode = "422",
                  description = "Validation error in the input data.",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidDataResponse.class))
          )
  })
  @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
  @PostMapping
  public ResponseEntity<UserResponse> create(@RequestBody CreateUserDto createUserDto)
          throws ResourceNotFoundException {
    log.info("Creating a new admin user with email: {}", createUserDto.getEmail());

    Role roleUser = roleService.findByName(ROLE_ADMIN);
    log.debug("Found role: {}", roleUser.getName());

    createUserDto.setRole(roleUser)
            .setConfirmed(true)
            .setEnabled(true);

    User user = userService.save(createUserDto);
    log.info("Admin user created successfully with ID: {}", user.getId());

    return ResponseEntity.ok(new UserResponse(user));
  }

  @Operation(
          summary = "Delete an admin user",
          description = "Deletes an admin user with the specified ID.",
          tags = {"Admin Management"}
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "204",
                  description = "Admin user deleted successfully.",
                  content = @Content
          ),
          @ApiResponse(
                  responseCode = "401",
                  description = "Unauthorized access.",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))
          ),
          @ApiResponse(
                  responseCode = "403",
                  description = "Forbidden access.",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))
          )
  })
  @PreAuthorize("hasRole('ROLE_SUPER_ADMIN')")
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable String id) {
    log.info("Deleting admin user with ID: {}", id);
    userService.delete(id);
    log.info("Admin user with ID: {} deleted successfully", id);

    return ResponseEntity.noContent().build();
  }
}