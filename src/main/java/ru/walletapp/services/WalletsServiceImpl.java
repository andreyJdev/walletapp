package ru.walletapp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.walletapp.dto.RequestWalletDTO;
import ru.walletapp.dto.ResponseWalletDTO;
import ru.walletapp.mappers.WalletsMapper;
import ru.walletapp.models.Wallet;
import ru.walletapp.repositories.WalletsRepository;

import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class WalletsServiceImpl implements WalletsService {

    private final WalletsRepository walletsRepository;
    private final WalletsMapper walletsMapper;

    public Optional<ResponseWalletDTO> findWalletById(UUID walletId) {
        Optional<Wallet> wallet = walletsRepository.findWalletByWalletId(walletId);
        return wallet.map(this.walletsMapper::walletToResponse);
    }

    @Transactional(readOnly = false)
    public ResponseWalletDTO performOperation(RequestWalletDTO request) {
        return null;
    }
}
