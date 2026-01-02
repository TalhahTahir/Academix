package com.talha.academix.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.talha.academix.dto.VaultTransactionDTO;
import com.talha.academix.model.VaultTransaction;

@Mapper(componentModel = "spring")
public interface VaultTransactionMapper {
    
    @Mapping(source = "vault.id", target = "vaultId")
    @Mapping(source = "initiater.userid", target = "initiaterId")
    VaultTransactionDTO toDto(VaultTransaction vaultTransaction);

    @Mapping(source = "vaultId", target = "vault.id")
    @Mapping(source = "initiaterId", target = "initiater.userid")
    VaultTransaction toEntity(VaultTransactionDTO dto);
}
