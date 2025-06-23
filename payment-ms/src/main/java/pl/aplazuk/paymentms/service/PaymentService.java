package pl.aplazuk.paymentms.service;

import brave.Span;
import brave.Tracer;
import org.springframework.stereotype.Service;
import pl.aplazuk.paymentms.dto.OrderDTO;
import pl.aplazuk.paymentms.model.PaymentStatus;


@Service
public class PaymentService {
    private final Tracer tracer;

    public PaymentService(Tracer tracer) {
        this.tracer = tracer;
    }

    public OrderDTO checkPaymentStatusForGivenOrder(Long orderId, String paymentMethod) {
        Span span = tracer.nextSpan().name("order-payment-processing").tag("paymentMethod", paymentMethod);
        span.start();
        try {
            if (orderId == null) {
                throw new NoOrderFoundException(String.format("No orders found for given paymentMethod: %s; Check order id: %s", paymentMethod, orderId));
            }
            return new OrderDTO(orderId, PaymentStatus.getRandomStatus(), paymentMethod);
        } finally {
            span.finish();
        }
    }
}
