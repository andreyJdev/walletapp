package ru.walletapp.mappers;

import org.springframework.stereotype.Component;
import ru.walletapp.dto.ResponseWalletDTO;
import ru.walletapp.models.Wallet;

import java.math.BigDecimal;

@Component
public class WalletsMapper {

    public ResponseWalletDTO walletToResponse(Wallet wallet) {
        ResponseWalletDTO response = new ResponseWalletDTO();
        response.setWalletId(wallet.getWalletId());
        response.setBalance(new BigDecimal(wallet.getBalance()));
        return response;
    }
}
