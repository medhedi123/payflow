package com.hedi.payflow.wallet.controller;

import com.hedi.payflow.wallet.dto.WalletResponse;
import com.hedi.payflow.wallet.service.WalletService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/wallets")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;

    @GetMapping("/me")
    public WalletResponse getMyWallet(Authentication authentication) {
        return walletService.getMyWallet(authentication);
    }
}