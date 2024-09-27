package ru.walletapp.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.walletapp.dto.RequestWalletDTO;
import ru.walletapp.dto.ResponseWalletDTO;
import ru.walletapp.mappers.WalletsMapper;
import ru.walletapp.models.Wallet;
import ru.walletapp.repositories.WalletsRepository;
import ru.walletapp.utils.InvalidJsonException;
import ru.walletapp.utils.LowBalanceException;
import ru.walletapp.utils.NotFoundWalletException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static ru.walletapp.enums.OperationType.WITHDRAW;

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
    public ResponseWalletDTO updateBalance(RequestWalletDTO request) {
        Wallet wallet = this.walletsRepository
                .findWalletByWalletIdWithLock(request.getWalletId())
                .orElseThrow(() -> new NotFoundWalletException("walletapp.errors.wallet.not_found"));

        BigDecimal currentBalance = new BigDecimal(wallet.getBalance());
        if (request.getOperationType() == WITHDRAW && request.getAmount().compareTo(currentBalance) > 0) {
            throw new LowBalanceException("walletapp.errors.wallet.low_balance");
        }

        currentBalance = performOperation(currentBalance, request);
        wallet.setBalance(currentBalance.toString());

        this.walletsRepository.save(wallet);

        return walletsMapper.walletToResponse(wallet);
    }

    public BigDecimal performOperation(BigDecimal currentBalance, RequestWalletDTO request) {

        switch (request.getOperationType()) {
            case DEPOSIT -> {
                return currentBalance.add(request.getAmount());
            }
            case WITHDRAW -> {
                return currentBalance.subtract(request.getAmount());
            }
            default -> throw new InvalidJsonException("walletapp.errors.wallet.bad_request");
        }
    }
}
