package pl.aplazuk.inventoryms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import pl.aplazuk.inventoryms.service.InventoryNotFoundException;

@RestControllerAdvice
public class InventoryExceptionHandler {

    @ExceptionHandler(InventoryNotFoundException.class)
    public ResponseEntity<String> handleResourceNotFound(InventoryNotFoundException exception) {
        String errors = exception.getMessage();
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errors);
    }
}
