package com.springSecurity.accessManagement.controllers;

import com.springSecurity.accessManagement.events.OnResetPasswordEvent;
import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.dtos.ForgotPasswordDto;
import com.springSecurity.accessManagement.models.dtos.ResetPasswordDto;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.models.entities.UserAccount;
import com.springSecurity.accessManagement.models.response.BadRequestResponse;
import com.springSecurity.accessManagement.models.response.InvalidDataResponse;
import com.springSecurity.accessManagement.models.response.SuccessResponse;
import com.springSecurity.accessManagement.services.interfaces.UserAccountService;
import com.springSecurity.accessManagement.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

import static com.springSecurity.accessManagement.constants.Constants.*;

@Tag(name = SWG_RESPWD_TAG_NAME, description = SWG_RESPWD_TAG_DESCRIPTION)
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/public/auth")
public class ResetPasswordController {

    @Autowired
    private UserService userService;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserAccountService userAccountService;

    @Operation(
            summary = "Forgot Password",
            description = "Sends a password reset link to the user's email address.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password reset link sent successfully.",
                            content = @Content(schema = @Schema(implementation = SuccessResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Unable to send the password reset link.",
                            content = @Content(schema = @Schema(implementation = BadRequestResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Invalid data provided.",
                            content = @Content(schema = @Schema(implementation = InvalidDataResponse.class))
                    )
            }
    )
    @PostMapping(value = "/forgot-password")
    public ResponseEntity<Map<String, String>> forgotPassword(@RequestBody ForgotPasswordDto forgotPasswordDto)
            throws ResourceNotFoundException {
        User user = userService.findByEmail(forgotPasswordDto.getEmail());
        Map<String, String> result = new HashMap<>();

        if (user == null) {
            result.put(MESSAGE_KEY, NO_USER_FOUND_WITH_EMAIL_MESSAGE);
            return ResponseEntity.badRequest().body(result);
        }

        eventPublisher.publishEvent(new OnResetPasswordEvent(user));
        //http://localhost:8082/reset-password?token=5888e625-e991-47e8-b623-8cbda65b2c74
        result.put(MESSAGE_KEY, PASSWORD_LINK_SENT_MESSAGE);
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "Reset Password",
            description = "Resets the user's password using the provided token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Password reset successfully.",
                            content = @Content(schema = @Schema(implementation = SuccessResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Unable to reset password.",
                            content = @Content(schema = @Schema(implementation = BadRequestResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Invalid data provided.",
                            content = @Content(schema = @Schema(implementation = InvalidDataResponse.class))
                    )
            }
    )
    @PostMapping(value = "/reset-password")
    public ResponseEntity<Map<String, String>> resetPassword(@RequestBody ResetPasswordDto passwordResetDto)
            throws ResourceNotFoundException {
        UserAccount userAccount = userAccountService.findByToken(passwordResetDto.getToken());
        Map<String, String> result = new HashMap<>();

        if (userAccount.isExpired()) {
            result.put(MESSAGE_KEY, TOKEN_EXPIRED_MESSAGE);
            userAccountService.delete(userAccount.getId().toString());
            return ResponseEntity.badRequest().body(result);
        }

        userService.updatePassword(userAccount.getUser().getId().toString(), passwordResetDto.getPassword());
        result.put(MESSAGE_KEY, RESET_PASSWORD_SUCCESS_MESSAGE);
        userAccountService.delete(userAccount.getId().toString());
        return ResponseEntity.ok(result);
    }
}
