package com.springSecurity.accessManagement.models.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;


@Accessors(chain = true)
@Setter
@Getter
public class LoginUserDto {


    @Email(message = "Email address is not valid")
    @NotBlank(message = "The email address is required")
    private String email;

    @Size(min = 6, message = "Must be at least 6 characters")
    private String password;
}
