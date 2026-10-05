package com.walletx.transferservice.persistence;

import com.walletx.transferservice.domain.Transfer;

import java.util.List;

public interface TransferRepository {

    Transfer save(Transfer transfer);

    List<Transfer> findAll();
}
