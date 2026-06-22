package com.hedi.payflow.wallet.service;

import com.hedi.payflow.user.entity.User;
import com.hedi.payflow.user.repository.UserRepository;
import com.hedi.payflow.wallet.dto.WalletResponse;
import com.hedi.payflow.wallet.entity.Wallet;
import com.hedi.payflow.wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    public WalletResponse getMyWallet(Authentication authentication) {
        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Wallet wallet = walletRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Wallet not found"));

        return new WalletResponse(
                wallet.getId(),
                user.getId(),
                wallet.getBalance(),
                wallet.getCurrency(),
                wallet.getStatus(),
                wallet.getCreatedAt()
        );
    }
}