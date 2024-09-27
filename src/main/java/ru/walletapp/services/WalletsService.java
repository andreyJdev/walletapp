package ru.walletapp.services;

import ru.walletapp.dto.RequestWalletDTO;
import ru.walletapp.dto.ResponseWalletDTO;

import java.util.Optional;
import java.util.UUID;

public interface WalletsService {
    Optional<ResponseWalletDTO> findWalletById(UUID walletId);

    ResponseWalletDTO performOperation(RequestWalletDTO request);
}
