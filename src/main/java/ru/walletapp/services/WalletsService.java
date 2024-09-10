package ru.walletapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.walletapp.dto.WalletRequestDTO;
import ru.walletapp.dto.WalletResponseDTO;
import ru.walletapp.models.Wallet;
import ru.walletapp.repositories.WalletsRepository;
import ru.walletapp.utils.WalletNotFoundException;
import ru.walletapp.utils.WalletIncorrectDataException;
import ru.walletapp.mappers.WalletsMapper;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class WalletsService {
    WalletsRepository walletsRepository;
    WalletsMapper walletsMapper;

    @Autowired
    public WalletsService(WalletsRepository walletsRepository, WalletsMapper walletsMapper) {
        this.walletsRepository = walletsRepository;
        this.walletsMapper = walletsMapper;
    }

    public WalletResponseDTO findByWalletId(UUID walletId) {
        Optional<Wallet> foundResult = walletsRepository.findByWalletId(walletId);
        if (foundResult.isPresent()) {
            return walletsMapper.convertToWalletDTO(foundResult.get());
        }
        throw new WalletNotFoundException("Пользователь с таким UUID не найден");
    }

    public WalletRequestDTO updateWalletDTO(WalletRequestDTO walletDTO) {
        Wallet wallet = walletsRepository.findByWalletId(walletDTO.getWalletId()).orElse(null);
        if (wallet == null) {
            throw new WalletNotFoundException("Кошелек с таким UUID не найден");
        }
        BigDecimal currentAmount = new BigDecimal(wallet.getAmount());
        switch (walletDTO.getOperationType()) {
            case DEPOSIT -> {
                walletDTO.setAmount(currentAmount.add(walletDTO.getAmount()));
            }
            case WITHDRAW -> {
                // если текущий баланс >= сумме снятия, то снятие одобряется
                if (currentAmount.compareTo(walletDTO.getAmount()) >= 0)
                    walletDTO.setAmount(currentAmount.subtract(walletDTO.getAmount()));
                else
                    throw new WalletIncorrectDataException("Недостаточно средств для снятия со счета");
            }
        }
        return walletDTO;
    }
    @Transactional
    public void updateWallet(WalletRequestDTO walletDTO) {
        WalletRequestDTO updatedDataDTO = updateWalletDTO(walletDTO);
        walletsRepository.save(walletsMapper.convertToWallet(updatedDataDTO));
    }
}
