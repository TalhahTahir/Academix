package com.talha.academix.model;

import java.util.List;

import com.talha.academix.enums.Role;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

 @OneToMany(mappedBy="student", fetch = FetchType.LAZY)
 private List<Enrollment> enrollments;

 @OneToMany(mappedBy="user", fetch=FetchType.LAZY)
 private List<Payment> payments;

 @OneToMany(mappedBy= "user", cascade=CascadeType.ALL, fetch=FetchType.LAZY)
 private Wallet wallet;
}
