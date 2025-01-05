package com.springSecurity.accessManagement.controllers;

import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.helper.JwtTokenUtil;
import com.springSecurity.accessManagement.models.dtos.RefreshTokenDto;
import com.springSecurity.accessManagement.models.dtos.ValidateTokenDto;
import com.springSecurity.accessManagement.models.entities.RefreshToken;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.models.response.AuthTokenResponse;
import com.springSecurity.accessManagement.models.response.BadRequestResponse;
import com.springSecurity.accessManagement.models.response.InvalidDataResponse;
import com.springSecurity.accessManagement.models.response.SuccessResponse;
import com.springSecurity.accessManagement.repositories.RefreshTokenRepository;
import com.springSecurity.accessManagement.services.interfaces.UserService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureException;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.springSecurity.accessManagement.constants.Constants.*;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/public/token")
@Slf4j
@Tag(name = SWG_TOKEN_TAG_NAME, description = SWG_TOKEN_TAG_DESCRIPTION)

public class TokenController {

  @Autowired
  private JwtTokenUtil jwtTokenUtil;

  @Autowired
  private RefreshTokenRepository refreshTokenRepository;

  @Autowired
  private UserService userService;

  @Operation(
          summary = "Validate a token",
          description = "Validates the provided token and returns the validation status.",
          tags = {SWG_TOKEN_TAG_NAME}
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Token validated successfully.",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = SuccessResponse.class))
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
  @PostMapping(value = "/validate")
  public ResponseEntity<Map<String, String>> validate(@RequestBody ValidateTokenDto validateTokenDto) {
    String username = null;
    Map<String, String> result = new HashMap<>();

    try {
      username = jwtTokenUtil.getUsernameFromToken(validateTokenDto.getToken());
    } catch (IllegalArgumentException e) {
      log.error(JWT_ILLEGAL_ARGUMENT_MESSAGE, e);
      result.put(MESSAGE_KEY, JWT_ILLEGAL_ARGUMENT_MESSAGE);
    } catch (ExpiredJwtException e) {
      log.warn(JWT_EXPIRED_MESSAGE, e);
      result.put(MESSAGE_KEY, JWT_EXPIRED_MESSAGE);
    } catch (SignatureException e) {
      log.error(JWT_SIGNATURE_MESSAGE);
      result.put(MESSAGE_KEY, JWT_SIGNATURE_MESSAGE);
    }

    if (username != null) {
      result.put(MESSAGE_KEY, VALIDATE_TOKEN_SUCCESS_MESSAGE);
      return ResponseEntity.ok(result);
    }

    return ResponseEntity.badRequest().body(result);
  }

  @Operation(
          summary = "Refresh a token",
          description = "Refreshes the provided refresh token and returns a new access token.",
          tags = {SWG_TOKEN_TAG_NAME}
  )
  @ApiResponses(value = {
          @ApiResponse(
                  responseCode = "200",
                  description = "Token refreshed successfully.",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = AuthTokenResponse.class))
          ),
          @ApiResponse(
                  responseCode = "400",
                  description = "Invalid token or user not found.",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = BadRequestResponse.class))
          ),
          @ApiResponse(
                  responseCode = "422",
                  description = "Validation error in the input data.",
                  content = @Content(mediaType = "application/json", schema = @Schema(implementation = InvalidDataResponse.class))
          )
  })
  @PostMapping(value = "/refresh")
  public ResponseEntity<Object> refresh(@RequestBody RefreshTokenDto refreshTokenDto)
          throws ResourceNotFoundException {
    RefreshToken refreshToken = refreshTokenRepository.findByValue(refreshTokenDto.getToken());
    Map<String, String> result = new HashMap<>();

    if (refreshToken == null) {
      result.put(MESSAGE_KEY, INVALID_TOKEN_MESSAGE);
      return ResponseEntity.badRequest().body(result);
    }

    User user = userService.findById(refreshToken.getId().toString());
    if (user == null) {
      result.put(MESSAGE_KEY, TOKEN_NOT_FOUND_MESSAGE);
      return ResponseEntity.badRequest().body(result);
    }

    String token = jwtTokenUtil.createTokenFromUser(user);
    Date expirationDate = jwtTokenUtil.getExpirationDateFromToken(token);

    return ResponseEntity.ok(new AuthTokenResponse(token, refreshToken.getValue(), expirationDate.getTime()));
  }
}
