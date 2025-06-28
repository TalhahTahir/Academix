package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.CreateUserDTO;
import com.talha.academix.dto.UserDTO;

public interface UserService {
    UserDTO createUser(CreateUserDTO dto);
    UserDTO getUserById(Long id);
    List<UserDTO> getAllUsers();
    void deleteUser(Long id);
    UserDTO updateUser(Long id, CreateUserDTO dto);
}
