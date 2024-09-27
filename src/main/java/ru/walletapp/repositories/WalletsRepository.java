package ru.walletapp.repositories;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.walletapp.models.Wallet;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface WalletsRepository extends JpaRepository<Wallet, UUID> {

    Optional<Wallet> findWalletByWalletId(UUID walletId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT w FROM Wallet w WHERE w.walletId = :walletId")
    Optional<Wallet> findWalletByWalletIdWithLock(UUID walletId);
}
