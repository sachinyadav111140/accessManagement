package com.springSecurity.accessManagement.models.dtos;



import com.springSecurity.accessManagement.constraints.FieldMatch;
import com.springSecurity.accessManagement.models.entities.Coordinates;
import com.springSecurity.accessManagement.models.entities.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embedded;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Schema(description = "Parameters required to create or update user")
@FieldMatch.List({
        @FieldMatch(first = "password", second = "confirmPassword", message = "The password fields must match")
})
@Accessors(chain = true)
@Setter
@Getter
public class CreateUserDto {

    @Schema(hidden = true)
    private String id;

    @Schema(description = "User first name", required = true)
    @NotBlank(message = "The first name is required")
    private String firstName;

    @Schema(description = "User last name", required = true)
    @NotBlank(message = "The last name is required")
    private String lastName;

    @Schema(description = "User email address", required = true)
    @Email(message = "Email address is not valid")
    @NotBlank(message = "The email address is required")
    private String email;

    @Schema(description = "User's password (must be at least 6 characters)", required = true)
    @Size(min = 6, message = "Must be at least 6 characters")
    private String password;

    @Schema(description = "User timezone", required = true)
    @NotBlank(message = "The timezone is required")
    private String timezone;

    @Schema(description = "Password confirmation", required = true)
    @NotBlank(message = "This field is required")
    private String confirmPassword;

    @Schema(description = "User gender")
    private String gender;

    private String avatar;

    @Schema(description = "Indicates if the user will be enabled or not",hidden = true)
    private boolean enabled;

    @Schema(description = "Indicates if the user has confirmed their account", hidden = true)

    private boolean confirmed;

    @Schema(description = "Geographic location of the user")
    @Embedded
    private Coordinates coordinates;

    @Schema(hidden = true)
    private Role role;

    public CreateUserDto() {
        enabled = true;
    }
}
