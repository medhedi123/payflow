package com.hedi.payflow.wallet.repository;

import com.hedi.payflow.user.entity.User;
import com.hedi.payflow.wallet.entity.Wallet;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WalletRepository extends JpaRepository<Wallet, Long> {

    Optional<Wallet> findByUser(User user);

    boolean existsByUser(User user);
}