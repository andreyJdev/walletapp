package ru.walletapp.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.walletapp.utils.NotFoundWalletException;

import java.util.Locale;

@RestControllerAdvice
@RequiredArgsConstructor
public class WalletsExceptionHandlerAdvice {

    private final MessageSource messageSource;

    @ExceptionHandler(NotFoundWalletException.class)
    public ResponseEntity<ProblemDetail> handleNotFoundWalletException(NotFoundWalletException e,
                                                                       Locale locale) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND,
                messageSource.getMessage(e.getMessage(), new Object[0], e.getMessage(), locale));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(problemDetail);
    }

}
