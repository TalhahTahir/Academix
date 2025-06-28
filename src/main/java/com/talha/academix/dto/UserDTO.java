package com.talha.academix.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private Long userid;
    private String username;
    private String gender;
    private String email;
    private String role;
    private String phone;
    private String image;
}
