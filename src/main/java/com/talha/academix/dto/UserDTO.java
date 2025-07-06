package com.talha.academix.dto;

import com.talha.academix.enums.Role;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userid;
    private String username;
    private String gender;
    private String email;
    private Role role;
    private String phone;
    private String image;
    // Relations are omitted or represented by IDs if needed
}
