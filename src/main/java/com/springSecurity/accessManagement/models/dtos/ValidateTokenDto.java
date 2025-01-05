package com.springSecurity.accessManagement.models.dtos;


import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;



@Accessors(chain = true)
@Setter
@Getter
public class ValidateTokenDto {
    @NotBlank(message = "The token is required")
    private String token;
}
