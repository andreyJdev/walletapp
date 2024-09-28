package ru.walletapp.mappers;

import org.junit.jupiter.api.Test;
import ru.walletapp.dto.ResponseWalletDTO;
import ru.walletapp.models.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class WalletsMapperTest {

    WalletsMapper mapper = new WalletsMapper();

    @Test
    void walletToResponse() {
        // given
        UUID walletId = UUID.randomUUID();
        Wallet wallet = new Wallet(walletId, "1000.0");
        // when
        ResponseWalletDTO response = mapper.walletToResponse(wallet);
        // then
        assertEquals(walletId, response.getWalletId());
        assertEquals(BigDecimal.valueOf(1000.0), response.getBalance());
    }
}