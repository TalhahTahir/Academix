package com.talha.academix.services;

import java.util.List;

import com.talha.academix.dto.WithdrawalDTO;
import com.talha.academix.dto.WithdrawalRequestDTO;

public interface WithdrawalService {
    WithdrawalDTO requestWithdrawal(WithdrawalRequestDTO req);

    WithdrawalDTO getById(Long withdrawalId);

    List<WithdrawalDTO> getByUser(Long userId);

    void handleTransferPaid(String transferId);

    void handleTransferFailed(String transferId);

    void handlePayoutPaid(String payoutId);

    void handlePayoutFailed(String payoutId);
}