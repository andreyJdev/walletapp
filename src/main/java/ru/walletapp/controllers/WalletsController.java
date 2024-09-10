package ru.walletapp.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.walletapp.dto.WalletRequestDTO;
import ru.walletapp.dto.WalletResponseDTO;
import ru.walletapp.services.WalletsService;
import ru.walletapp.utils.WalletErrorResponse;
import ru.walletapp.utils.WalletIncorrectDataException;
import ru.walletapp.utils.WalletNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
public class WalletsController {
    WalletsService walletsService;

    public WalletsController(WalletsService walletsService) {
        this.walletsService = walletsService;
    }

    @GetMapping("/wallets/{WALLET_UUID}")
    public ResponseEntity<WalletResponseDTO> showWalletById(
            @PathVariable("WALLET_UUID") UUID walletId) {
        WalletResponseDTO response = walletsService.findByWalletId(walletId);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/wallet")
    public ResponseEntity<HttpStatus> updateWallet(
            @RequestBody @Valid WalletRequestDTO walletDTO,
            BindingResult bindingResult) {
        // если есть ошибки при заполнении, выводится первая
        if (bindingResult.hasErrors()) {
            String errorMsg = bindingResult.getFieldErrors().get(0).getDefaultMessage();
            throw new WalletIncorrectDataException(errorMsg);
        }
        walletsService.updateWallet(walletDTO);
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @ExceptionHandler
    private ResponseEntity<WalletErrorResponse> handleException(WalletNotFoundException e) {
        WalletErrorResponse response;
        response = new WalletErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler
    private ResponseEntity<WalletErrorResponse> handleException(WalletIncorrectDataException e) {
        WalletErrorResponse response = new WalletErrorResponse(
                e.getMessage(),
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler
    public ResponseEntity<WalletErrorResponse> handleException(HttpMessageNotReadableException e) {
        WalletErrorResponse response = new WalletErrorResponse(
                "Невалидный JSON файл",
                LocalDateTime.now()
        );
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
}
