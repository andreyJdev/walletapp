package ru.walletapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import ru.walletapp.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class WalletRequestDTO {
    @NotNull(message = "Поле 'walletId' не должно быть пустым")
    private UUID walletId;

    @NotNull(message = "Поле 'operationType' не должно быть пустым")
    private OperationType operationType;

    @NotNull(message = "Поле 'amount' не должно быть пустым")
    @Positive(message = "Поле 'amount' только положительное")
    private BigDecimal amount;
}
