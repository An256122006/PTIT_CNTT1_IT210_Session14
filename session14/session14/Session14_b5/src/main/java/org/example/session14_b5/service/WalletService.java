package org.example.session14_b5.service;

import org.example.session14_b5.exception.ValidationException;
import org.example.session14_b5.model.Wallet;
import org.example.session14_b5.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class WalletService {

    private final WalletRepository walletRepository;

    public WalletService(WalletRepository walletRepository) {
        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    public Wallet getWalletByUserId(long userId) {
        return walletRepository.findByUserId(userId)
                .orElseThrow(() -> new ValidationException("Không tìm thấy ví của user ID " + userId));
    }

    @Transactional
    public Wallet deposit(long userId, BigDecimal amount) {
        if (amount == null || amount.signum() <= 0) {
            throw new ValidationException("Số tiền nạp phải lớn hơn 0");
        }
        Wallet wallet = getWalletByUserId(userId);
        walletRepository.deposit(userId, amount);
        return walletRepository.findByUserId(userId).orElse(wallet);
    }
}

