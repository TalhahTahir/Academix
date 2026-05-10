package com.talha.academix.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.talha.academix.dto.VaultDTO;
import com.talha.academix.model.Vault;

@Mapper(componentModel = "spring")
public interface VaultMapper {
    
    @Mapping(source = "user.userid", target = "userId")
    VaultDTO toDto(Vault vault);

    @Mapping(source = "userId", target = "user.userid")
    Vault toEntity(VaultDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateVaultfromDto(VaultDTO dto, @MappingTarget Vault v);
}
