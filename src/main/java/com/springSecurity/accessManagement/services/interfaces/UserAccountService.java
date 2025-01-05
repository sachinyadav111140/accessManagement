package com.springSecurity.accessManagement.services.interfaces;



import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.models.entities.UserAccount;

import java.util.List;

public interface UserAccountService {
    UserAccount save(User user, String token);

    List<UserAccount> findAll();

    void delete(String id);

    UserAccount findByToken(String token) throws ResourceNotFoundException;

    UserAccount findById(String id) throws ResourceNotFoundException;
}
