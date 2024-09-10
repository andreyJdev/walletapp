package ru.walletapp.mappers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.walletapp.dto.WalletRequestDTO;
import ru.walletapp.dto.WalletResponseDTO;
import ru.walletapp.enums.OperationType;
import ru.walletapp.models.Wallet;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@ExtendWith(MockitoExtension.class)
class WalletsMapperTest {
    WalletsMapper walletsMapper;

    @BeforeEach
    void setUp() {
        walletsMapper = new WalletsMapper();
    }

    @Test
    void convertWalletResponseDtoToWalletTest() {
        UUID walletId = UUID.randomUUID();
        String amount = "1000.0000";
        Wallet wallet = new Wallet(walletId, amount);
        WalletResponseDTO target = new WalletResponseDTO(walletId, new BigDecimal("1000.0000"));

        WalletResponseDTO response = walletsMapper.convertToWalletDTO(wallet);

        assertThat(response).isEqualTo(target);
    }

    @Test
    void convertWalletRequestDtoToWalletTest() {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal("1000.0000");
        WalletRequestDTO request = new WalletRequestDTO(walletId, OperationType.DEPOSIT, amount);
        Wallet target = new Wallet(walletId, "1000.0000");

        Wallet wallet = walletsMapper.convertToWallet(request);

        assertThat(wallet).isEqualTo(target);
    }
}