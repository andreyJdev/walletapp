package ru.walletapp.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.walletapp.dto.RequestWalletDTO;
import ru.walletapp.dto.ResponseWalletDTO;
import ru.walletapp.services.WalletsService;
import ru.walletapp.utils.NotFoundWalletException;

import java.util.UUID;

@RestController
@RequestMapping("api/v1")
@RequiredArgsConstructor
public class WalletsController {

    private final WalletsService walletsService;

    @GetMapping("wallets/{walletId}")
    public ResponseEntity<ResponseWalletDTO> getWallet(@PathVariable UUID walletId) {
        ResponseWalletDTO response = this.walletsService.findWalletById(walletId)
                .orElseThrow(() -> new NotFoundWalletException("walletapp.errors.wallet.not_found"));
        return ResponseEntity.ok().body(response);
    }

    @PostMapping("wallet")
    public ResponseEntity<ResponseWalletDTO> updateWallet(@RequestBody @Valid RequestWalletDTO request,
                                                          BindingResult bindingResult) throws BindException {
        if (bindingResult.hasErrors()) {
            if (bindingResult instanceof BindException e)
                throw e;
            throw new BindException(bindingResult);
        }

        ResponseWalletDTO response = this.walletsService.updateBalance(request);
        return ResponseEntity.ok().body(response);
    }
}
