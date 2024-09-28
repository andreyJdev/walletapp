package ru.walletapp.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.MapBindingResult;
import ru.walletapp.utils.LowBalanceException;
import ru.walletapp.utils.NotFoundWalletException;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class WalletsExceptionHandlerAdviceTest {

    @Mock
    MessageSource messageSource;

    @InjectMocks
    WalletsExceptionHandlerAdvice controller;

    @Test
    public void handleNotFoundWalletException_ResultsNotFound() {
        // given
        var e = new NotFoundWalletException("error_code");
        var locale = new Locale("ru");
        when(messageSource.getMessage(e.getMessage(),
                new Object[0], e.getMessage(), locale))
                .thenReturn("Кошелек не найден");
        // when
        var result = this.controller.handleNotFoundWalletException(e, locale);
        // then
        assertNotNull(result);
        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals("Кошелек не найден", result.getBody().getDetail());
    }

    @Test
    void handleLowBalanceException_ResultsBadRequest() {
        // given
        var e = new LowBalanceException("error_code");
        var locale = new Locale("ru");
        when(messageSource.getMessage(e.getMessage(),
                new Object[0], e.getMessage(), locale))
                .thenReturn("На балансе недостаточно средств");
        // when
        var result = this.controller.handleLowBalanceException(e, locale);
        // then
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals("На балансе недостаточно средств",
                result.getBody().getDetail());
    }

    @Test
    void handleInvalidJsonException_ResultsBadRequest() {
        // given
        var e = new HttpMessageNotReadableException("error_code");
        var locale = new Locale("ru");
        when(messageSource.getMessage("walletapp.errors.wallet.bad_request",
                new Object[0], "walletapp.errors.wallet.bad_request", locale))
                .thenReturn("Невалидный JSON");
        // when
        var result = this.controller.handleInvalidJsonException(e, locale);
        // then
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals("Невалидный JSON",
                result.getBody().getDetail());
    }

    @Test
    void handleBindException_ResultsBadRequest() {
        // given
        var bindingResult = new MapBindingResult(Map.of(), "request");
        bindingResult.addError(new FieldError("request", "title",
                "Поле '...' имеет ошибки валидации"));
        var e = new BindException(bindingResult);
        var locale = new Locale("ru");
        when(messageSource.getMessage("walletapp.errors.wallet.bad_request",
                new Object[0], "walletapp.errors.wallet.bad_request", locale))
                .thenReturn("Ошибки валидации");
        // when
        var result = this.controller.handleBindException(e, locale);
        // then
        assertNotNull(result);
        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals("Ошибки валидации", result.getBody().getDetail());
        assertEquals(List.of(new FieldError("request", "title",
                        "Поле '...' имеет ошибки валидации")),
                e.getFieldErrors());
    }

    @Test
    void handleInternalException_ResultsInternalServerError() {
        // given
        var e = new Exception("error_code");
        var locale = new Locale("ru");
        when(messageSource.getMessage("walletapp.errors.wallet.internal_error",
                new Object[0], "walletapp.errors.wallet.internal_error", locale))
                .thenReturn("Внутренняя ошибка сервера");
        // when
        var result = this.controller.handleInternalException(e, locale);
        // then
        assertNotNull(result);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertInstanceOf(ProblemDetail.class, result.getBody());
        assertEquals("Внутренняя ошибка сервера",
                result.getBody().getDetail());
    }
}