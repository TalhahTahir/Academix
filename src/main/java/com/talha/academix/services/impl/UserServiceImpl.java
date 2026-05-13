package com.talha.academix.services.impl;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.talha.academix.dto.CreateUserDTO;
import com.talha.academix.dto.UserDTO;
import com.talha.academix.dto.VaultDTO;
import com.talha.academix.enums.Role;
import com.talha.academix.exception.ResourceNotFoundException;
import com.talha.academix.mapper.UserMapper;
import com.talha.academix.model.User;
import com.talha.academix.repository.UserRepo;
import com.talha.academix.services.UserService;
import com.talha.academix.services.VaultService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final VaultService vaultService;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    @Override
    public UserDTO createUser(CreateUserDTO dto) {
        User user = userMapper.createUser(dto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setCreatedAt(Instant.now());

        user = userRepo.save(user);
        if ((user.getRole() == Role.ADMIN) || (user.getRole() == Role.TEACHER)) {
            VaultDTO vaultDto = new VaultDTO();
            vaultDto.setUserId(user.getUserid());
            vaultDto.setAvailableBalance(BigDecimal.ZERO);
            vaultDto.setTotalEarned(BigDecimal.ZERO);
            vaultDto.setTotalWithdrawn(BigDecimal.ZERO);
            vaultDto.setCurrency("USD");
            vaultDto.setCreatedAt(Instant.now());
            vaultDto.setUpdatedAt(Instant.now());
            vaultService.createVault(vaultDto);
        }
        return userMapper.toDto(user);
    }

    @Override
    public UserDTO getUserById(Long id) {
        User user = userRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id : " + id));
        return userMapper.toDto(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepo.findAll();
        return users.stream()
                .map(user -> userMapper.toDto(user))
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

        userMapper.updateUserFromDto(dto, user);
        user.setUserid(id);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        User updatedUser = userRepo.save(user);
        return userMapper.toDto(updatedUser);
    }
}
