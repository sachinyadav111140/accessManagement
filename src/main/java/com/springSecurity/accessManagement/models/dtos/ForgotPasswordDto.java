package com.springSecurity.accessManagement.models.dtos;


import com.springSecurity.accessManagement.constraints.Exists;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Schema(description = "Parameters required to request a reset link")
@Exists.List({
        @Exists(property = "email", repository = "UserRepository", message = "This email doesn't exist in the db!")
})
@Accessors(chain = true)
@Setter
@Getter
public class ForgotPasswordDto {

    @Schema(description = "The email address to send the link to", required = true)
    @Email(message = "Email address is not valid")
    @NotBlank(message = "The email address is required")
    private String email;
}
