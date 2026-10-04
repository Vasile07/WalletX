package com.walletx.walletservice.persistence;

import com.walletx.walletservice.domain.WalletEntity;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {

    List<WalletEntity> findAllByUserId(UUID userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select wallet from WalletEntity wallet where wallet.id = :walletId and wallet.userId = :userId")
    Optional<WalletEntity> findByIdAndUserIdForUpdate(
            @Param("walletId") UUID walletId,
            @Param("userId") UUID userId
    );
}
