package ru.walletapp.mappers;

import org.springframework.stereotype.Component;
import ru.walletapp.dto.WalletRequestDTO;
import ru.walletapp.dto.WalletResponseDTO;
import ru.walletapp.models.Wallet;

import java.math.BigDecimal;

@Component
public class WalletsMapper {
    public WalletResponseDTO convertToWalletDTO(Wallet wallet){
        WalletResponseDTO walletDTO = new WalletResponseDTO();
        walletDTO.setWalletId(wallet.getWalletId());
        walletDTO.setAmount(new BigDecimal(wallet.getAmount()));
        return walletDTO;
    }

    public Wallet convertToWallet(WalletRequestDTO walletDTO){
        Wallet wallet = new Wallet();
        wallet.setWalletId(walletDTO.getWalletId());
        wallet.setAmount(walletDTO.getAmount().toString());
        return wallet;
    }
}
