package ru.walletapp.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.walletapp.dto.WalletRequestDTO;
import ru.walletapp.dto.WalletResponseDTO;
import ru.walletapp.enums.OperationType;
import ru.walletapp.services.WalletsService;
import ru.walletapp.utils.WalletIncorrectDataException;
import ru.walletapp.utils.WalletNotFoundException;

import java.math.BigDecimal;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class WalletsControllerTest {
    @Mock
    private WalletsService walletsService;
    @InjectMocks
    private WalletsController walletsController;
    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(walletsController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void getById_ReturnValidJson() throws Exception {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(1000000.100099);
        WalletResponseDTO response = new WalletResponseDTO(walletId, amount);

        when(walletsService.findByWalletId(walletId)).thenReturn(response);

        mockMvc.perform(get("/api/v1/wallets/{WALLET_UUID}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.amount").value(amount));
    }

    @Test
    void getIncorrectId_WalletNotFoundExceptionTest() throws Exception {
        UUID walletId = UUID.randomUUID();

        when(walletsService.findByWalletId(walletId)).thenThrow(new WalletNotFoundException("Пользователь с таким UUID не найден"));

        mockMvc.perform(get("/api/v1/wallets/{WALLET_UUID}", walletId))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof WalletNotFoundException));
    }

    @Test
    void postCorrectData_UpdateWalletControllerTest() throws Exception {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(1000000.100099);
        OperationType operationType = OperationType.DEPOSIT;
        WalletRequestDTO request = new WalletRequestDTO(walletId, operationType, amount);
        String requestJson = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());
        verify(walletsService, times(1)).updateWallet(request);
    }

    @Test
    void postIncorrectData_WalletIncorrectDataExceptionTest() throws Exception {
        UUID walletId = UUID.randomUUID();
        BigDecimal amount = new BigDecimal(-1000000.100099);
        OperationType operationType = OperationType.DEPOSIT;
        WalletRequestDTO invalidRequest = new WalletRequestDTO(walletId, operationType, amount);
        String requestJson = objectMapper.writeValueAsString(invalidRequest);

        mockMvc.perform(post("/api/v1/wallet").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof WalletIncorrectDataException))
                .andExpect(status().isBadRequest());
        verify(walletsService, times(0)).updateWallet(invalidRequest);
    }

    @Test
    void postInvalidJson_HttpMessageNotReadableException() throws Exception {
        String requestJson = "it's JSON";

        mockMvc.perform(post("/api/v1/wallet").contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof HttpMessageNotReadableException))
                .andExpect(status().isBadRequest());
    }
}