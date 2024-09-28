package ru.walletapp.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import ru.walletapp.dto.RequestWalletDTO;
import ru.walletapp.dto.ResponseWalletDTO;
import ru.walletapp.enums.OperationType;
import ru.walletapp.services.WalletsService;
import ru.walletapp.utils.NotFoundWalletException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WalletsControllerTest {

    @Mock
    WalletsService walletsService;

    @InjectMocks
    WalletsController walletsController;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletsController).build();
    }

    @Test
    public void getById_RequestIsValid_ReturnWallet() {
        // given
        UUID walletId = UUID.randomUUID();
        BigDecimal balance = BigDecimal.valueOf(10000);
        ResponseWalletDTO responseWalletDTO = new ResponseWalletDTO(walletId, balance);
        when(walletsService.findWalletById(walletId)).thenReturn(Optional.of(responseWalletDTO));
        // when
        ResponseEntity<ResponseWalletDTO> response = walletsController.getWallet(walletId);
        // then
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(responseWalletDTO, response.getBody());
    }

    @Test
    public void getById_RequestIsInvalid_ReturnNotFoundWalletException() {
        // given
        UUID walletId = UUID.randomUUID();
        when(walletsService.findWalletById(walletId)).thenReturn(Optional.empty());
        // when
        var exception = assertThrows(NotFoundWalletException.class, () -> this.walletsController.getWallet(walletId));
        // then
        assertEquals("walletapp.errors.wallet.not_found", exception.getMessage());
    }

    @Test
    public void updateProduct_RequestIsValid_ReturnsResponseEntity() throws BindException {
        // given
        UUID walletId = UUID.randomUUID();
        OperationType operationType = OperationType.DEPOSIT;
        BigDecimal amount = BigDecimal.valueOf(10000);
        RequestWalletDTO request = new RequestWalletDTO(walletId.toString(), operationType, amount);
        BindingResult bindingResult = new MapBindingResult(Map.of(), "request");
        when(walletsService.updateBalance(request)).thenReturn(new ResponseWalletDTO(walletId, amount));
        // when
        ResponseEntity<ResponseWalletDTO> result = this.walletsController.updateWallet(request, bindingResult);
        // then
        assertNotNull(result.getBody());
        assertEquals(HttpStatus.OK, result.getStatusCode());
        verify(this.walletsService).updateBalance(request);
    }

    @Test
    public void updateProduct_RequestIsInvalid_ReturnsBadRequest() throws Exception {
        // given
        String requestJson = "it's JSON";
        // when
        mockMvc.perform(post("/api/v1/wallet").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                // then
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void updateProduct_RequestIsInvalidUseBindException_ReturnsBadRequest() {
        // given
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = BigDecimal.valueOf(-10000);
        OperationType operationType = OperationType.DEPOSIT;
        RequestWalletDTO request = new RequestWalletDTO(walletId.toString(), operationType, amount);
        BindingResult bindingResult = new MapBindingResult(Map.of(), "request");
        bindingResult.addError(new FieldError("request", "title", "error"));
        // when
        BindException exception = assertThrows(BindException.class, () -> this.walletsController.updateWallet(request, bindingResult));
        // then
        assertEquals(List.of(new FieldError("request", "title", "error")), exception.getAllErrors());
        verifyNoInteractions(this.walletsService);
    }
}