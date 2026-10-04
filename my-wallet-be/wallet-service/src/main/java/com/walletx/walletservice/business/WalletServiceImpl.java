package com.walletx.walletservice.business;

import com.walletx.walletservice.domain.WalletEntity;
import com.walletx.walletservice.persistence.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

    private final WalletRepository walletRepository;

    @Override
    public List<WalletEntity> findAllByUserId(UUID userId) {
        return walletRepository.findAllByUserId(Objects.requireNonNull(userId, "userId must not be null"));
    }
}
