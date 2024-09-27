package ru.walletapp.dto;

import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.walletapp.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestWalletDTO {

    private UUID walletId;

    private OperationType operationType;

    @Positive(message = "{walletapp.errors.wallet.not_negative_amount}")
    private BigDecimal amount;
}
