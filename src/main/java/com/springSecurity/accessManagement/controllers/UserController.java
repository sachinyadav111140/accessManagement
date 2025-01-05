package com.springSecurity.accessManagement.controllers;

import com.springSecurity.accessManagement.exceptions.PasswordNotMatchException;
import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.dtos.UpdatePasswordDto;
import com.springSecurity.accessManagement.models.dtos.UpdateUserDto;
import com.springSecurity.accessManagement.models.dtos.UpdateUserPermissionDto;
import com.springSecurity.accessManagement.models.entities.Permission;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.models.response.*;
import com.springSecurity.accessManagement.services.FileStorageServiceImpl;
import com.springSecurity.accessManagement.services.interfaces.PermissionService;
import com.springSecurity.accessManagement.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Arrays;
import java.util.Optional;

import static com.springSecurity.accessManagement.constants.Constants.*;

@Tag(name = SWG_USER_TAG_NAME, description = SWG_USER_TAG_DESCRIPTION)
@RestController
@RequestMapping(value = "/public/users")
@Validated
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Autowired
    private FileStorageServiceImpl fileStorageServiceImpl;

    @Operation(
            summary = SWG_USER_LIST_OPERATION,
            description = SWG_USER_LIST_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_USER_LIST_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserListResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "403", description = INVALID_DATA_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class)))
            }
    )
    @PreAuthorize("hasAuthority('read:users')")
    @GetMapping
    public ResponseEntity<UserListResponse> all(){
        return ResponseEntity.ok(new UserListResponse(userService.findAll()));
    }

    @Operation(
            summary = SWG_USER_LOGGED_OPERATION,
            description = SWG_USER_LOGGED_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_USER_LOGGED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class)))
            }
    )
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponse> currentUser() throws ResourceNotFoundException {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return ResponseEntity.ok(new UserResponse(userService.findByEmail(authentication.getName())));
    }

    @Operation(
            summary = SWG_USER_ITEM_OPERATION,
            description = SWG_USER_ITEM_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_USER_ITEM_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class)))
            }
    )
    @PreAuthorize("hasAuthority('read:user')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> one(@PathVariable String id)
            throws ResourceNotFoundException {
       return ResponseEntity.ok(new UserResponse(userService.findById(id)));
    }

    @Operation(
            summary = SWG_USER_UPDATE_OPERATION,
            description = SWG_USER_UPDATE_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidDataResponse.class)))
            }
    )
    @PreAuthorize("isAuthenticated()")
    @PutMapping
    public ResponseEntity<UserResponse> update( @RequestBody UpdateUserDto updateUserDto)
            throws ResourceNotFoundException {
        Authentication authentication =SecurityContextHolder.getContext().getAuthentication();
        User user = userService.findByEmail(authentication.getName());
        return ResponseEntity.ok(new UserResponse(userService.update(user.getId().toString(), updateUserDto)));
    }

    @Operation(
            summary = SWG_USER_UPDATE_PWD_OPERATION,
            description = SWG_USER_UPDATE_PWD_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_USER_UPDATE_PWD_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "400", description = SWG_USER_UPDATE_PWD_ERROR, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidDataResponse.class)))
            }
    )
    @PreAuthorize("hasAuthority('change:password')")
    @PutMapping("/{id}/password")
    public ResponseEntity<UserResponse> updatePassword(
            @PathVariable String id,  @RequestBody UpdatePasswordDto updatePasswordDto
    ) throws PasswordNotMatchException, ResourceNotFoundException {
        User user = userService.updatePassword(id, updatePasswordDto);

        if (user == null) {
            throw new PasswordNotMatchException(PASSWORD_NOT_MATCH_MESSAGE);
        }

        return ResponseEntity.ok(new UserResponse(user));
    }

    @Operation(
            summary = SWG_USER_DELETE_OPERATION,
            description = SWG_USER_DELETE_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "204", description = SWG_USER_DELETE_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class)))
            }
    )
    @PreAuthorize("hasAuthority('delete:user')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = SWG_USER_PICTURE_OPERATION,
            description = SWG_USER_PICTURE_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_USER_PICTURE_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "400", description = SWG_USER_PICTURE_ERROR, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))),
                    @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidDataResponse.class)))
            }
    )
    @PreAuthorize("hasAuthority('change:picture')")
    @PostMapping("/{id}/picture")
    public ResponseEntity<UserResponse> uploadPicture(
            @PathVariable String id,
            @RequestParam(name = "file", required = false) MultipartFile file,
            @RequestParam("action")
            @Parameter(description = "Action to perform on the picture (u for upload, d for delete)")
            @Pattern(regexp = "[ud]", message = "The valid value can be \"u\" or \"d\"")
            @Size(max = 1, message = "This field length can't be greater than 1")
            @NotBlank(message = "This field is required")
            String action
    ) throws IOException, ResourceNotFoundException {
        User user = null;
        UpdateUserDto updateUserDto = new UpdateUserDto();

        if (action.equals("u")) {
            String fileName = fileStorageServiceImpl.storeFile(file);
            updateUserDto.setAvatar(fileName);
            user = userService.update(id, updateUserDto);
        } else if (action.equals("d")) {
            user = userService.findById(id);

            if (user.getAvatar() != null) {
                boolean deleted = fileStorageServiceImpl.deleteFile(user.getAvatar());

                if (deleted) {
                    user.setAvatar(null);
                    userService.update(user);
                }
            }
        } else {
            log.info(USER_PICTURE_NO_ACTION_MESSAGE);
        }

        return ResponseEntity.ok().body(new UserResponse(user));
    }

    @Operation(
            summary = SWG_USER_PERMISSION_ASSIGN_OPERATION,
            description = SWG_USER_PERMISSION_ASSIGN_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_USER_PERMISSION_ASSIGN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidDataResponse.class)))
            }
    )
    @PreAuthorize("hasAuthority('assign:permission')")
    @PutMapping("/{id}/permissions")
    public ResponseEntity<UserResponse> assignPermissions(@PathVariable String id,  @RequestBody UpdateUserPermissionDto updateUserPermissionDto)
            throws ResourceNotFoundException {
        User user = userService.findById(id);

        Arrays.stream(updateUserPermissionDto.getPermissions()).forEach(permissionName -> {
            Optional<Permission> permission = permissionService.findByName(permissionName);

            if (permission.isPresent() && !user.hasPermission(permissionName)) {
                user.addPermission(permission.get());
            }
        });

        userService.update(user);

        return ResponseEntity.ok().body(new UserResponse(user));
    }

    @Operation(
            summary = SWG_USER_PERMISSION_REVOKE_OPERATION,
            description = SWG_USER_PERMISSION_REVOKE_MESSAGE,
            responses = {
                    @ApiResponse(responseCode = "200", description = SWG_USER_PERMISSION_REVOKE_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "401", description = UNAUTHORIZED_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "403", description = FORBIDDEN_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))),
                    @ApiResponse(responseCode = "422", description = INVALID_DATA_MESSAGE, content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidDataResponse.class)))
            }
    )
    @PreAuthorize("hasAuthority('revoke:permission')")
    @DeleteMapping("/{id}/permissions")
    public ResponseEntity<User> revokePermissions(@PathVariable String id,  @RequestBody UpdateUserPermissionDto updateUserPermissionDto)
            throws ResourceNotFoundException {
        User user = userService.findById(id);

        Arrays.stream(updateUserPermissionDto.getPermissions()).forEach(permissionName -> {
            Optional<Permission> permission = permissionService.findByName(permissionName);

            if (permission.isPresent() && user.hasPermission(permissionName)) {
                user.removePermission(permission.get());
            }
        });

        userService.update(user);
        return ResponseEntity.ok().body(user);
    }
}
