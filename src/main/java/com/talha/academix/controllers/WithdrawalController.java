package com.talha.academix.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.talha.academix.dto.WithdrawalDTO;
import com.talha.academix.dto.WithdrawalRequestDTO;
import com.talha.academix.services.WithdrawalService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/withdrawals")
@RequiredArgsConstructor
@Validated
public class WithdrawalController {

    private final WithdrawalService withdrawalService;

    @PostMapping
    public ResponseEntity<WithdrawalDTO> request(@Valid @RequestBody WithdrawalRequestDTO req) {
        return ResponseEntity.ok(withdrawalService.requestWithdrawal(req));
    }

    @GetMapping("/{id}")
    public ResponseEntity<WithdrawalDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(withdrawalService.getById(id));
    }

    @GetMapping("/users/{userId}")
    public ResponseEntity<List<WithdrawalDTO>> getByUser(@PathVariable Long userId) {
        return ResponseEntity.ok(withdrawalService.getByUser(userId));
    }
}