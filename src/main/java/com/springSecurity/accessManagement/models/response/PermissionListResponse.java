package com.springSecurity.accessManagement.models.response;



import com.springSecurity.accessManagement.models.entities.Permission;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@Setter
@Getter
public class PermissionListResponse {
    private List<Permission> data;
}
