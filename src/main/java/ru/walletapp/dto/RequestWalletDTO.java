package ru.walletapp.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @NotNull(message = "{walletapp.errors.wallet.not_null_wallet_id}")
    @Pattern(regexp = "^[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}$",
            message = "{walletapp.errors.wallet.invalid_wallet_id}")
    private String walletId;

    @NotNull(message = "{walletapp.errors.wallet.not_null_operation_type}")
    private OperationType operationType;

    @NotNull(message = "{walletapp.errors.wallet.not_null_amount}")
    @Positive(message = "{walletapp.errors.wallet.not_negative_amount}")
    private BigDecimal amount;
}
