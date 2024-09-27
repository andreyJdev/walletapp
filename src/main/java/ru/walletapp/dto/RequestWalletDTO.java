package ru.walletapp.dto;

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
    private BigDecimal amount;
}
