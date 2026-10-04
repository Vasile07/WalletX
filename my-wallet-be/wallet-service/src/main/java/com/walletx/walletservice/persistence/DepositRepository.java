package com.walletx.walletservice.persistence;

import com.walletx.walletservice.domain.DepositEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DepositRepository extends JpaRepository<DepositEntity, UUID> {

    List<DepositEntity> findAllByUserIdOrderByCompletedAtDesc(UUID userId);
}
