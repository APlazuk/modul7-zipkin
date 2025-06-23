package pl.aplazuk.paymentms.controller;

import brave.Span;
import brave.Tracer;
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
    private final Tracer tracer;

    public PaymentController(PaymentService paymentService, Tracer tracer) {
        this.paymentService = paymentService;
        this.tracer = tracer;
    }

    @GetMapping("/hello")
    public ResponseEntity<String> getString() {
        return ResponseEntity.ok("Hello World");
    }


    @GetMapping("/check")
    public ResponseEntity<OrderDTO> checkPaymentStatusForOrderById(@RequestParam Long orderId, @RequestParam String paymentMethod) {
        Span span = tracer.currentSpan().tag("paymentMethod", paymentMethod);
        span.start();
        try {
            OrderDTO orderDTO = paymentService.checkPaymentStatusForGivenOrder(orderId, paymentMethod);
            return ResponseEntity.ok(orderDTO);
        }finally {
            span.finish();
        }
    }
}
