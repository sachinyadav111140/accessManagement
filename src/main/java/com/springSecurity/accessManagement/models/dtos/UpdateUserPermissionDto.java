package com.springSecurity.accessManagement.models.dtos;


import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;



@Accessors(chain = true)
@Setter
@Getter
public class UpdateUserPermissionDto {

    private String[] permissions;
}
