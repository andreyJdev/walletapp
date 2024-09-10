package ru.walletapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.walletapp.dto.WalletRequestDTO;
import ru.walletapp.enums.OperationType;
import ru.walletapp.repositories.WalletsRepository;
import ru.walletapp.utils.WalletNotFoundException;
import ru.walletapp.mappers.WalletsMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class WalletsServiceWithoutDBTest {
    @Mock
    private WalletsRepository walletsRepository;
    @Mock
    private WalletsMapper walletsMapper;
    private WalletsService walletsService;

    // перед каждым тестом
    @BeforeEach
    void setUp() {
        walletsService = new WalletsService(walletsRepository, walletsMapper);
    }

    @Test
    void ifWalletNotFound_WalletNotFoundException_AndNeverSaveInDbTest() {
        UUID walletId = UUID.randomUUID();
        WalletRequestDTO requestForDontExistsWallet =
                new WalletRequestDTO(walletId, OperationType.DEPOSIT, new BigDecimal(1000.0));

        assertThatThrownBy(() -> walletsService.updateWallet(requestForDontExistsWallet))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessageContaining("Кошелек с таким UUID не найден");
        verify(walletsRepository, never()).save(any());
    }
}
