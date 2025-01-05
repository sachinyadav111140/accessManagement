package com.springSecurity.accessManagement.controllers;

import com.springSecurity.accessManagement.events.OnRegistrationCompleteEvent;
import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.helper.Helpers;
import com.springSecurity.accessManagement.helper.JwtTokenUtil;
import com.springSecurity.accessManagement.models.dtos.CreateUserDto;
import com.springSecurity.accessManagement.models.dtos.LoginUserDto;
import com.springSecurity.accessManagement.models.dtos.ValidateTokenDto;
import com.springSecurity.accessManagement.models.entities.RefreshToken;
import com.springSecurity.accessManagement.models.entities.Role;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.models.entities.UserAccount;
import com.springSecurity.accessManagement.models.response.*;
import com.springSecurity.accessManagement.repositories.RefreshTokenRepository;
import com.springSecurity.accessManagement.services.interfaces.RoleService;
import com.springSecurity.accessManagement.services.interfaces.UserAccountService;
import com.springSecurity.accessManagement.services.interfaces.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.springSecurity.accessManagement.constants.Constants.*;

@Tag(name = "Authentication", description = "Endpoints for user authentication and account management")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/public/auth")
@Slf4j
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private RoleService roleService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtTokenUtil jwtTokenUtil;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private UserAccountService userAccountService;

    @Operation(
            summary = "Register a new user",
            description = "Registers a new user and sends a confirmation email.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User registered successfully.",
                            content = @Content(schema = @Schema(implementation = UserResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Unable to register user.",
                            content = @Content(schema = @Schema(implementation = BadRequestResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Invalid data provided.",
                            content = @Content(schema = @Schema(implementation = InvalidDataResponse.class))
                    )
            }
    )
    @PostMapping(value = "/register")
    public ResponseEntity<Object> register(@RequestBody CreateUserDto createUserDto) {
        try {
            Role roleUser = roleService.findByName(ROLE_USER);
            createUserDto.setRole(roleUser);
            User user = userService.save(createUserDto);
           eventPublisher.publishEvent(new OnRegistrationCompleteEvent(user));
            return ResponseEntity.ok(user);
        } catch (ResourceNotFoundException e) {
            Map<String, String> result = new HashMap<>();
            result.put("message", SWG_AUTH_REGISTER_ERROR);
            log.error("Register User: " + ROLE_NOT_FOUND_MESSAGE);
            return ResponseEntity.badRequest().body(result);
        }
    }

    @Operation(
            summary = "User Login",
            description = "Authenticates the user and returns a JWT token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User authenticated successfully.",
                            content = @Content(schema = @Schema(implementation = AuthTokenResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Login failed.",
                            content = @Content(schema = @Schema(implementation = BadRequestResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "422",
                            description = "Invalid data provided.",
                            content = @Content(schema = @Schema(implementation = InvalidDataResponse.class))
                    )
            }
    )
    @PostMapping(value = "/login")
    public ResponseEntity<Object> login(@RequestBody LoginUserDto loginUserDto) throws ResourceNotFoundException {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginUserDto.getEmail(),
                        loginUserDto.getPassword()
                )
        );

        User user = userService.findByEmail(loginUserDto.getEmail());
        Map<String, String> result = new HashMap<>();

        if (!user.isEnabled()) {
            result.put(DATA_KEY, ACCOUNT_DEACTIVATED_MESSAGE);
            return ResponseEntity.badRequest().body(result);
        }

        if (!user.isConfirmed()) {
            result.put(DATA_KEY, ACCOUNT_NOT_CONFIRMED_MESSAGE);
            return ResponseEntity.badRequest().body(result);
        }

        SecurityContextHolder.getContext().setAuthentication(authentication);

        final String token = jwtTokenUtil.createTokenFromAuth(authentication);
        Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);
        String refreshToken = Helpers.generateRandomString(25);

        refreshTokenRepository.save(new RefreshToken(refreshToken));

        return ResponseEntity.ok(new AuthTokenResponse(token, refreshToken, expirationDate.getTime()));
    }

    @Operation(
            summary = "Confirm Account",
            description = "Confirms a user's account using a token.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Account confirmed successfully.",
                            content = @Content(schema = @Schema(implementation = SuccessResponse.class))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request. Token expired or invalid.",
                            content = @Content(schema = @Schema(implementation = BadRequestResponse.class))
                    )
            }
    )
    @PostMapping(value = "/confirm-account")
    public ResponseEntity<Object> confirmAccount(@RequestBody ValidateTokenDto validateTokenDto)
            throws ResourceNotFoundException {
        UserAccount userAccount = userAccountService.findByToken(validateTokenDto.getToken());
        Map<String, String> result = new HashMap<>();

        if (userAccount.isExpired()) {
            result.put(MESSAGE_KEY, TOKEN_EXPIRED_MESSAGE);
            userAccountService.delete(userAccount.getId().toString());
            return ResponseEntity.badRequest().body(result);
        }

        userService.confirm(userAccount.getUser().getId().toString());
        result.put(MESSAGE_KEY, ACCOUNT_CONFIRMED_MESSAGE);
        return ResponseEntity.ok(result);
    }
}
