package com.springSecurity.accessManagement.models.response;



import com.springSecurity.accessManagement.models.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class RoleResponse {
    private Role data;
}
