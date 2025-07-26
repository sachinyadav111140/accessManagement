package com.springSecurity.accessManagement.controllers;

import com.springSecurity.accessManagement.exceptions.ResourceNotFoundException;
import com.springSecurity.accessManagement.models.dtos.CreateUserDto;
import com.springSecurity.accessManagement.models.entities.Role;
import com.springSecurity.accessManagement.models.entities.User;
import com.springSecurity.accessManagement.models.response.UserResponse;
import com.springSecurity.accessManagement.services.interfaces.RoleService;
import com.springSecurity.accessManagement.services.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.times;

class AdminControllerTest {
    @Mock
    private RoleService roleService;

    @Mock
    private UserService userService;

    @InjectMocks
    private AdminController adminController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void create_createsAdminUserSuccessfully() throws ResourceNotFoundException {
        CreateUserDto createUserDto = new CreateUserDto();
        Role roleAdmin = new Role();
        roleAdmin.setName("ROLE_ADMIN");
        User user = new User();
        user.setId(1L);
        user.setEmail("admin");

        when(roleService.findByName("ROLE_ADMIN")).thenReturn(roleAdmin);
        when(userService.save(createUserDto)).thenReturn(user);

        ResponseEntity<UserResponse> response = adminController.create(createUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("admin", response.getBody().getData().getEmail());
    }
    @Test
    void create_throwsResourceNotFoundException() throws ResourceNotFoundException {
        CreateUserDto createUserDto = new CreateUserDto();

        when(roleService.findByName("ROLE_ADMIN")).thenThrow(new ResourceNotFoundException("Role not found"));

        assertThrows(ResourceNotFoundException.class, () -> adminController.create(createUserDto));
    }
    @Test
    void delete_deletesAdminUserSuccessfully() {
        String userId = "1";

        doNothing().when(userService).delete(userId);

        ResponseEntity<Void> response = adminController.delete(userId);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        verify(userService, times(1)).delete(userId);
    }

}