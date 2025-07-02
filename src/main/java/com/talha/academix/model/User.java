package com.talha.academix.model;

import com.talha.academix.enums.Role;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long userid;
 
 @NotEmpty
 private String username;

 @NotEmpty
 private String gender;

 @NotEmpty
 private String password;

 @NotEmpty
 private String email;

@NotEmpty
 @Enumerated(EnumType.STRING)
 private Role role;

 @NotEmpty
 private String phone;
 
 @NotEmpty
 private String image;

}
