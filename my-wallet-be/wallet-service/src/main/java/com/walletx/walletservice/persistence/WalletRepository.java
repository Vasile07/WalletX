package com.walletx.walletservice.persistence;

import com.walletx.walletservice.domain.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {

    List<WalletEntity> findAllByUserId(UUID userId);
}
