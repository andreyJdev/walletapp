package ru.walletapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.walletapp.models.Wallet;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletsRepository extends JpaRepository<Wallet, UUID> {
    Optional<Wallet> findByWalletId(UUID walletId);
}
