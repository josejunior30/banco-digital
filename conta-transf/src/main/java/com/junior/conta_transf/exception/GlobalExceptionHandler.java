package com.junior.conta_transf.exception;

import java.time.Instant;
import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import com.junior.conta_transf.DTO.ApiError;
import com.junior.conta_transf.DTO.FieldValidationError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class GlobalExceptionHandler {

 @ExceptionHandler(BusinessException.class)
 public ResponseEntity<ApiError> handleBusiness(BusinessException ex, HttpServletRequest request) {
     return build(
             HttpStatus.UNPROCESSABLE_ENTITY,
             "Regra de negócio",
             ex.getMessage(),
             request.getRequestURI(),
             null
     );
 }

 @ExceptionHandler(IllegalArgumentException.class)
 public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex, HttpServletRequest request) {
     return build(
             HttpStatus.BAD_REQUEST,
             "Requisição inválida",
             ex.getMessage(),
             request.getRequestURI(),
             null
     );
 }

 @ExceptionHandler(ExternalServiceUnavailableException.class)
 public ResponseEntity<ApiError> handleExternalServiceUnavailable(
         ExternalServiceUnavailableException ex,
         HttpServletRequest request
 ) {
     return build(
             HttpStatus.SERVICE_UNAVAILABLE,
             "Serviço indisponível",
             ex.getMessage(),
             request.getRequestURI(),
             null
     );
 }

 @ExceptionHandler(ClienteNaoEncontradoException.class)
 public ResponseEntity<ApiError> handleClienteNaoEncontrado(
         ClienteNaoEncontradoException ex,
         HttpServletRequest request
 ) {
     return build(
             HttpStatus.NOT_FOUND,
             "Cliente não encontrado",
             ex.getMessage(),
             request.getRequestURI(),
             null
     );
 }

 @ExceptionHandler(DataIntegrityViolationException.class)
 public ResponseEntity<ApiError> handleDataIntegrity(
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
 public ResponseEntity<ApiError> handleValidation(
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
 public ResponseEntity<ApiError> handleNotReadable(
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

 
 @ExceptionHandler({ NoHandlerFoundException.class, NoResourceFoundException.class })
 public ResponseEntity<ApiError> handleNotFound(Exception ex, HttpServletRequest request) {
     return build(
             HttpStatus.NOT_FOUND,
             "Recurso não encontrado",
             "Endpoint não encontrado",
             request.getRequestURI(),
             null
     );
 }

 @ExceptionHandler(Exception.class)
 public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
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

 private ResponseEntity<ApiError> build(
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
     return ResponseEntity.status(status).body(body);
 }
}