package com.walletx.transferservice.business;

import com.walletx.transferservice.domain.Transfer;

import java.util.List;

public interface TransferService {

    Transfer createTransfer(Transfer transfer);

    List<Transfer> findAllTransfers();
}
