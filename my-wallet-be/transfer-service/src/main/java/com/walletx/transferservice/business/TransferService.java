package com.walletx.transferservice.business;

import com.walletx.transferservice.controller.TransferRequest;
import com.walletx.transferservice.domain.Transfer;

import java.util.List;
import java.util.UUID;

public interface TransferService {

    Transfer createTransfer(TransferRequest request, String authorizationHeader);

    List<Transfer> findAllTransfers();

    List<Transfer> findTransfersForUser(UUID currentUserId, String authorizationHeader);
}
