package com.springSecurity.accessManagement.models.response;



import com.springSecurity.accessManagement.models.entities.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class UserResponse {
    private User data;
}
