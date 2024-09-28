package ru.walletapp.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.walletapp.dto.RequestWalletDTO;
import ru.walletapp.dto.ResponseWalletDTO;
import ru.walletapp.enums.OperationType;
import ru.walletapp.mappers.WalletsMapper;
import ru.walletapp.models.Wallet;
import ru.walletapp.repositories.WalletsRepository;
import ru.walletapp.utils.LowBalanceException;
import ru.walletapp.utils.NotFoundWalletException;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WalletsServiceImplTest {

    @Mock
    WalletsRepository walletsRepository;

    @Mock
    WalletsMapper walletsMapper;

    @InjectMocks
    WalletsServiceImpl walletsService;

    @Test
    void findWalletById_WalletExists_AccessFindById() {
        // given
        UUID walletId = UUID.randomUUID();
        String balance = "10000.0";
        Optional<Wallet> wallet = Optional.of(new Wallet(walletId, balance));
        Optional<ResponseWalletDTO> expectedResponse = Optional
                .of(new ResponseWalletDTO(walletId, new BigDecimal(balance)));
        when(this.walletsRepository.findWalletByWalletId(walletId)).thenReturn(wallet);
        when(this.walletsMapper.walletToResponse(wallet.get())).thenReturn(expectedResponse.get());
        // when
        Optional<ResponseWalletDTO> result = this.walletsService.findWalletById(walletId);
        // then
        assertEquals(expectedResponse, result);
        verify(this.walletsRepository).findWalletByWalletId(walletId);
        verifyNoMoreInteractions(this.walletsRepository);
    }

    @Test
    void updateBalance_WalletExistsAndHaveMoney_AccessUpdate() {
        // given
        UUID walletId = UUID.randomUUID();
        RequestWalletDTO request = new RequestWalletDTO(walletId.toString(),
                OperationType.WITHDRAW, new BigDecimal("5000.0"));
        when(this.walletsRepository.findWalletByWalletIdWithLock(UUID.fromString(request.getWalletId())))
                .thenReturn(Optional.of(new Wallet(walletId, "10000.0")));
        Wallet updatedWallet = new Wallet(walletId, "5000.0");
        when(this.walletsRepository.save(updatedWallet)).thenReturn(null);
        when(this.walletsMapper.walletToResponse(updatedWallet))
                .thenReturn(new ResponseWalletDTO(walletId, new BigDecimal("5000.0")));
        // when
        ResponseWalletDTO response = this.walletsService.updateBalance(request);
        // then
        assertEquals(new ResponseWalletDTO(walletId, new BigDecimal("5000.0")), response);
        verify(this.walletsRepository).findWalletByWalletIdWithLock(walletId);
        verify(this.walletsRepository).save(updatedWallet);
        verifyNoMoreInteractions(this.walletsRepository);
    }

    @Test
    void updateBalance_WalletNotExists_NotFoundWalletException() {
        // given
        UUID walletId = UUID.randomUUID();
        RequestWalletDTO request = new RequestWalletDTO();
        request.setWalletId(walletId.toString());
        // when
        NotFoundWalletException e = assertThrows(NotFoundWalletException.class,
                () -> this.walletsService.updateBalance(request));
        // then
        assertEquals("walletapp.errors.wallet.not_found", e.getMessage());
        verify(this.walletsRepository).findWalletByWalletIdWithLock(walletId);
        verifyNoMoreInteractions(this.walletsRepository);
    }

    @Test
    void updateBalance_WalletExistsAndLowBalanceForTransaction_LowBalanceException() {
        // given
        UUID walletId = UUID.randomUUID();
        RequestWalletDTO request = new RequestWalletDTO(walletId.toString(),
                OperationType.WITHDRAW, new BigDecimal("20000.0"));
        when(this.walletsRepository.findWalletByWalletIdWithLock(UUID.fromString(request.getWalletId())))
                .thenReturn(Optional.of(new Wallet(walletId, "10000.0")));
        // when
        LowBalanceException e = assertThrows(LowBalanceException.class,
                () -> this.walletsService.updateBalance(request));
        // then
        assertEquals("walletapp.errors.wallet.low_balance", e.getMessage());
        verify(this.walletsRepository).findWalletByWalletIdWithLock(walletId);
        verifyNoMoreInteractions(this.walletsRepository);
    }

    @Test
    void performOperation_OperationTypeDeposit_CorrectAddition() {
        // given
        BigDecimal balance = BigDecimal.valueOf(10000);
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(5000);
        RequestWalletDTO request = new RequestWalletDTO(walletId.toString(), OperationType.DEPOSIT, amount);
        // when
        BigDecimal newBalance = this.walletsService.performOperation(balance, request);
        // then
        assertEquals(new BigDecimal(15000), newBalance);
    }

    @Test
    void performOperation_OperationTypeWithdraw_CorrectSubtraction() {
        // given
        BigDecimal balance = BigDecimal.valueOf(10000);
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(5000);
        RequestWalletDTO request = new RequestWalletDTO(walletId.toString(), OperationType.WITHDRAW, amount);
        // when
        BigDecimal newBalance = this.walletsService.performOperation(balance, request);
        // then
        assertEquals(new BigDecimal(5000), newBalance);
    }
}