package pl.aplazuk.paymentms.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pl.aplazuk.paymentms.dto.OrderDTO;
import pl.aplazuk.paymentms.service.PaymentService;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> getString() {
        return ResponseEntity.ok("Hello World");
    }


    @GetMapping("/check")
    public ResponseEntity<OrderDTO> checkPaymentStatusForOrderById(@RequestParam Long orderId, @RequestParam String paymentMethod) {
        OrderDTO orderDTO = paymentService.checkPaymentStatusForGivenOrder(orderId, paymentMethod);
        return ResponseEntity.ok(orderDTO);
    }
}
