package com.springSecurity.accessManagement.services;


import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.models.entities.UserAccount;
import com.springSecurity.accessManagement.repositories.UserAccountRepository;
import com.springSecurity.accessManagement.services.interfaces.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.springSecurity.accessManagement.constants.Constants.INVALID_TOKEN_MESSAGE;
import static com.springSecurity.accessManagement.constants.Constants.RESOURCE_NOT_FOUND_MESSAGE;


@Service(value = "userAccountService")
public class UserAccountServiceImpl implements UserAccountService {
    @Autowired
    private UserAccountRepository userAccountRepository;

    @Override
    public UserAccount save(User user, String token) {
        UserAccount newUserAccount = new UserAccount();
        Date dateNow = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(dateNow);
        c.add(Calendar.DATE, 2);

        newUserAccount.setUser(user)
                .setToken(token)
                .setExpireAt(c.getTime().getTime());

        return userAccountRepository.save(newUserAccount);
    }

    @Override
    public List<UserAccount> findAll() {
        List<UserAccount> list = new ArrayList<>();
        userAccountRepository.findAll().iterator().forEachRemaining(list::add);

        return list;
    }

    @Override
    public void delete(String id) {
        userAccountRepository.deleteById(Long.parseLong(id));
    }

    @Override
    public UserAccount findByToken(String token) throws ResourceNotFoundException {
        Optional<UserAccount> userAccountOptional = userAccountRepository.findByToken(token);

        if (userAccountOptional.isEmpty()) {
            throw new ResourceNotFoundException(INVALID_TOKEN_MESSAGE);
        }

        return userAccountOptional.get();
    }

    @Override
    public UserAccount findById(String id) throws ResourceNotFoundException {
        Optional<UserAccount> confirmAccountOptional = userAccountRepository.findById(Long.parseLong(id));

        if (confirmAccountOptional.isEmpty()) {
            throw new ResourceNotFoundException(RESOURCE_NOT_FOUND_MESSAGE);
        }
        return confirmAccountOptional.get();
    }
}
