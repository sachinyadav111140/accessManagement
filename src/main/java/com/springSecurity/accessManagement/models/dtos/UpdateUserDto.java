package com.springSecurity.accessManagement.models.dtos;


import com.springSecurity.accessManagement.models.entities.Coordinates;
import com.springSecurity.accessManagement.models.entities.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Embedded;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.Set;

@Schema(description = "Parameters required to update a user")
@Accessors(chain = true)
@Setter
@Getter
public class UpdateUserDto {

    @Schema(description = "User first name")
    private String firstName;

    @Schema(description = "User last name")
    private String lastName;

    @Schema(description = "User timezone")
    private String timezone;

    @Schema(description = "User gender")
    private String gender;

    @Schema(description = "User avatar URL")
    private String avatar;

    @Schema(description = "Indicates if the user will be enabled or not")
    private boolean enabled;

    @Schema(description = "Indicates if the user has confirmed their account")
    private boolean confirmed;

    @Schema(description = "Geographic location of the user")
    @Embedded
    private Coordinates coordinates;

    @Schema(description = "Roles assigned to the user",hidden = true)
    private Set<Role> roles;
}
