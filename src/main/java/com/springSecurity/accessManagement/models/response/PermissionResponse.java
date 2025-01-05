package com.springSecurity.accessManagement.models.response;



import com.springSecurity.accessManagement.models.entities.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class PermissionResponse {
    private Permission data;
}
