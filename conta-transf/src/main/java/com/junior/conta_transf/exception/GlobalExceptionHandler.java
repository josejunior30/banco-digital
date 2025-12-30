package com.junior.conta_transf.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.handler.annotation.support.MethodArgumentNotValidException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.junior.conta_transf.DTO.ApiError;
import com.junior.conta_transf.DTO.FieldValidationError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BusinessException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleBusiness(
            BusinessException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Regra de negócio",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "Requisição inválida",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleIllegalState(
            IllegalStateException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                "Estado inválido",
                ex.getMessage(),
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleDataIntegrity(
            DataIntegrityViolationException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.CONFLICT,
                "Violação de integridade",
                "Operação não permitida (integridade)",
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        List<FieldValidationError> fields = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toField)
                .toList();

        return build(
                HttpStatus.BAD_REQUEST,
                "Validação",
                "Campos inválidos",
                request.getRequestURI(),
                fields
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public org.springframework.http.ResponseEntity<ApiError> handleNotReadable(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.BAD_REQUEST,
                "JSON inválido",
                "Body inválido ou malformado",
                request.getRequestURI(),
                null
        );
    }

    @ExceptionHandler(Exception.class)
    public org.springframework.http.ResponseEntity<ApiError> handleGeneric(
            Exception ex,
            HttpServletRequest request
    ) {
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno",
                "Ocorreu um erro inesperado",
                request.getRequestURI(),
                null
        );
    }

    private FieldValidationError toField(FieldError fe) {
        String msg = fe.getDefaultMessage() == null ? "inválido" : fe.getDefaultMessage();
        return new FieldValidationError(fe.getField(), msg);
    }

    private org.springframework.http.ResponseEntity<ApiError> build(
            HttpStatus status,
            String error,
            String message,
            String path,
            List<FieldValidationError> fields
    ) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                error,
                message,
                path,
                fields
        );
        return org.springframework.http.ResponseEntity.status(status).body(body);
    }
}