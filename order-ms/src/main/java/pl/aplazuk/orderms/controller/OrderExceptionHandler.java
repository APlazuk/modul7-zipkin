package pl.aplazuk.orderms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.aplazuk.orderms.service.NoOrderFoundException;
import pl.aplazuk.orderms.service.NoProductsFoundException;

@RestControllerAdvice
public class OrderExceptionHandler {

    @ExceptionHandler(NoOrderFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(NoOrderFoundException exception) {
        String error = exception.getResponseBody();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(NoProductsFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(NoProductsFoundException exception) {
        String error = exception.getResponseBody();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }
}
