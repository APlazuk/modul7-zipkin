package pl.aplazuk.paymentms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    @GetMapping("/hello")
    public ResponseEntity<String> getString() {
        return ResponseEntity.ok("Hello World");
    }


}
