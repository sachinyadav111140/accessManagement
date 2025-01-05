package com.springSecurity.accessManagement.models.dtos;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;



@Accessors(chain = true)
@Setter
@Getter
public class UpdatePasswordDto {
    @Size(min = 6, message = "Must be at least 6 characters")
    @NotBlank(message = "This field is required")
    private String currentPassword;
    @Size(min = 6, message = "Must be at least 6 characters")
    @NotBlank(message = "This field is required")
    private String newPassword;
}
