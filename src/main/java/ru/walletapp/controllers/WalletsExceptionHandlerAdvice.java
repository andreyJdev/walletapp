package ru.walletapp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.walletapp.utils.LowBalanceException;
import ru.walletapp.utils.NotFoundWalletException;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class WalletsExceptionHandlerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(NotFoundWalletException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundWalletException(NotFoundWalletException e, Locale locale) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                this.messageSource.getMessage(e.getMessage(),
                        new Object[0], e.getMessage(), locale));

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

    @ExceptionHandler(LowBalanceException.class)
    public ResponseEntity<ProblemDetail> handleLowBalanceException(LowBalanceException e, Locale locale) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                this.messageSource.getMessage(e.getMessage(),
                        new Object[0], e.getMessage(), locale));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ProblemDetail> handleInvalidJsonException(HttpMessageNotReadableException e, Locale locale) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                this.messageSource.getMessage("walletapp.errors.wallet.bad_request",
                        new Object[0], "walletapp.errors.wallet.bad_request", locale));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(problemDetail);
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ProblemDetail> handleBindException(BindException e, Locale locale) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
                this.messageSource.getMessage("walletapp.errors.wallet.bad_request",
                        new Object[0], "walletapp.errors.wallet.bad_request", locale));

        problemDetail.setProperty("errors", e.getBindingResult().getAllErrors()
                .stream()
                .map(ObjectError::getDefaultMessage)
                .toList());

        return ResponseEntity.badRequest().body(problemDetail);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleInternalException(Exception e, Locale locale) {

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
                this.messageSource.getMessage("walletapp.errors.wallet.internal_error",
                        new Object[0], "walletapp.errors.wallet.internal_error", locale));

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(problemDetail);
    }
}
