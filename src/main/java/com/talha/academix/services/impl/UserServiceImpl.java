package com.talha.academix.services.impl;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.CreateUserDTO;
import com.talha.academix.dto.UserDTO;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.model.User;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.UserService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final ModelMapper mapper;

    @Override
    public UserDTO createUser(CreateUserDTO dto) {
        User user = mapper.map(dto, User.class);
        user = userRepo.save(user);
        return mapper.map(user, UserDTO.class);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));
        return mapper.map(user, UserDTO.class);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(user -> mapper.map(user, UserDTO.class))
                .toList();
    }

    @Override
    public void deleteUser(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));
        userRepo.delete(user);
    }

    @Override
    public UserDTO updateUser(Long id, CreateUserDTO dto) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));

        user.setUsername(dto.getUsername());
        user.setGender(dto.getGender());
        user.setPassword(dto.getPassword());
        user.setEmail(dto.getEmail());
        user.setPhone(dto.getPhone());
        user.setImage(dto.getImage());
        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(dto.getPassword());
        }

        User updatedUser = userRepo.save(user);
        return mapper.map(updatedUser, UserDTO.class);
    }

}
