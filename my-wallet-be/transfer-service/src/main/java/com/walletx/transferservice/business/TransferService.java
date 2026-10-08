package com.walletx.transferservice.business;

import com.walletx.transferservice.controller.TransferRequest;
import com.walletx.transferservice.domain.Transfer;

import java.util.List;

public interface TransferService {

    Transfer createTransfer(TransferRequest request, String authorizationHeader);

    List<Transfer> findAllTransfers();
}
