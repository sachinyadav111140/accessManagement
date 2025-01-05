package com.springSecurity.accessManagement.models.dtos;



import com.springSecurity.accessManagement.constraints.FieldMatch;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@FieldMatch.List({
    @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
})
@Accessors(chain = true)
@Setter
@Getter
public class ResetPasswordDto {
    @NotBlank(message = "The token is required")
    private String token;

    @Size(min = 6, message = "Must be at least 6 characters")
    @NotBlank(message = "This field is required")
    private String password;

    @NotBlank(message = "This field is required")
    private String confirmPassword;
}
