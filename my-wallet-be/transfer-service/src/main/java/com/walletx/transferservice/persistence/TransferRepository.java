package com.walletx.transferservice.persistence;

import com.walletx.transferservice.domain.Transfer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface TransferRepository extends JpaRepository<Transfer, UUID> {

    List<Transfer> findAllBySenderWalletIdInOrReceiverWalletIdInOrderByCompletedAtDesc(
            Collection<UUID> senderWalletIds,
            Collection<UUID> receiverWalletIds
    );
}
