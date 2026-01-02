package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.WithdrawalDTO;
import com.talha.academix.model.Withdrawal;

@Mapper(componentModel = "spring")
public interface WithdrawalMapper {
    
    @Mapping(source = "vault.id", target = "vaultId")
    @Mapping(source = "requestedBy.userid", target = "requestedById")
    WithdrawalDTO toDto(Withdrawal withdrawal);

    @Mapping(source = "vaultId", target = "vault.id")
    @Mapping(source = "requestedById", target = "requestedBy.userid")
    Withdrawal toEntity(WithdrawalDTO dto);
}
