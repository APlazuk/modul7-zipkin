package pl.aplazuk.paymentms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.aplazuk.paymentms.service.NoOrderFoundException;

@RestControllerAdvice
public class PaymentExceptionHandler {

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(NoOrderFoundException exception) {
        String error = exception.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
