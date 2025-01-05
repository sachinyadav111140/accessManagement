package com.springSecurity.accessManagement.models.dtos;

import com.springSecurity.accessManagement.models.entities.Role;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Schema(description = "Parameters required to create role")
@Accessors(chain = true)
@Setter
@Getter
public class CreateRoleDto {

    @Schema(description = "Name of the role")
    @NotBlank(message = "The name is required")
    private String name;

    @Schema(description = "Description of the role")
    private String description;

    @Schema(description = "Whether the role is default or not")
    private boolean isDefault;

    public Role toRole() {
        return new Role()
                .setName(this.name)
                .setDescription(this.description)
                .setDefault(this.isDefault);
    }
}
