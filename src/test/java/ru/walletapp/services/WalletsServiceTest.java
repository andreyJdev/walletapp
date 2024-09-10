package ru.walletapp.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.walletapp.dto.WalletRequestDTO;
import ru.walletapp.dto.WalletResponseDTO;
import ru.walletapp.enums.OperationType;
import ru.walletapp.models.Wallet;
import ru.walletapp.repositories.WalletsRepository;
import ru.walletapp.utils.WalletIncorrectDataException;
import ru.walletapp.utils.WalletNotFoundException;
import ru.walletapp.mappers.WalletsMapper;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class WalletsServiceTest {
    @Autowired
    private WalletsRepository walletsRepository;
    @Mock
    private WalletsMapper walletsMapper;
    private WalletsService walletsService;

    @BeforeEach
    void setUp() {
        walletsService = new WalletsService(walletsRepository, walletsMapper);
    }

    @Test
    void walletExistsInDb_SuccessFoundWalletTest() {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(10000.000000);
        Wallet walletTarget = new Wallet(walletId, amount.toString());
        walletsRepository.save(walletTarget);
        WalletResponseDTO afterMapping = new WalletResponseDTO(walletId, amount);

        when(walletsMapper.convertToWalletDTO(walletTarget)).thenReturn(afterMapping);
        WalletResponseDTO response = walletsService.findByWalletId(walletId);

        assertThat(response).isEqualTo(afterMapping);
    }

    @Test
    void walletNotExistsInDb_WalletNotFoundExceptionTest() {
        UUID walletId = UUID.randomUUID();

        assertThatThrownBy(() -> walletsService.findByWalletId(walletId))
                .isInstanceOf(WalletNotFoundException.class)
                .hasMessageContaining("Пользователь с таким UUID не найден");
    }

    @Test
    void updateWalletDepositTest() {
        UUID walletId = UUID.randomUUID();
        // в данный момент на счету
        BigDecimal amountInBank = new BigDecimal(10000.00000);
        Wallet currentWallet = new Wallet(walletId, amountInBank.toString());
        walletsRepository.save(currentWallet);
        // взнос amount
        OperationType operationType = OperationType.DEPOSIT;
        BigDecimal amount = new BigDecimal(1000.0001);
        WalletRequestDTO deposit = new WalletRequestDTO(walletId, operationType, amount);
        WalletRequestDTO expectedResult = new WalletRequestDTO(walletId, operationType, amountInBank.add(amount));

        // обновленный счет
        WalletRequestDTO result = walletsService.updateWalletDTO(deposit);

        assertEquals(expectedResult.getWalletId(), result.getWalletId());
        assertEquals(expectedResult.getOperationType(), result.getOperationType());
        assertTrue(result.getAmount().compareTo(expectedResult.getAmount()) == 0);
    }

    @Test
    void updateWalletWithdrawTest_ApprovedTest() {
        UUID walletId = UUID.randomUUID();

        BigDecimal amountInBank = new BigDecimal(10000.00000);
        Wallet currentWallet = new Wallet(walletId, amountInBank.toString());
        walletsRepository.save(currentWallet);
        // съем меньше баланса
        OperationType operationType = OperationType.WITHDRAW;
        BigDecimal amount = new BigDecimal(1000.0001);
        WalletRequestDTO deposit = new WalletRequestDTO(walletId, operationType, amount);
        WalletRequestDTO expectedResult = new WalletRequestDTO(walletId, operationType, amountInBank.subtract(amount));

        WalletRequestDTO result = walletsService.updateWalletDTO(deposit);
    }

    @Test
    void updateWalletWithdrawTest_NotApprovedTest() {
        UUID walletId = UUID.randomUUID();

        BigDecimal amountInBank = new BigDecimal(1000.0001);
        Wallet currentWallet = new Wallet(walletId, amountInBank.toString());
        walletsRepository.save(currentWallet);
        // съем больше баланса
        OperationType operationType = OperationType.WITHDRAW;
        BigDecimal amount = new BigDecimal(10000.00000);
        WalletRequestDTO deposit = new WalletRequestDTO(walletId, operationType, amount);

        assertThatThrownBy(() -> walletsService.updateWalletDTO(deposit))
                .isInstanceOf(WalletIncorrectDataException.class)
                .hasMessageContaining("Недостаточно средств для снятия со счета");
    }

    @Test
    void updateWalletTest() {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(10000.000000);
        WalletRequestDTO calculatedData = new WalletRequestDTO(walletId, OperationType.DEPOSIT, amount);
        Wallet afterMapping = new Wallet(walletId, amount.toString());
        walletsRepository.save(afterMapping);

        // данные, которые уже были посчитаны другим методом
        when(walletsMapper.convertToWallet(calculatedData))
                .thenReturn(afterMapping);
        walletsService.updateWallet(calculatedData);

        // этот тест нужен, чтобы убедиться,
        // что данные из updateWalletDTO успешно сохраняются в БД
        assertThat(walletsRepository.findByWalletId(walletId).get())
                .isEqualTo(afterMapping);
    }
}