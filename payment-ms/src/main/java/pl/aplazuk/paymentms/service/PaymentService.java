package pl.aplazuk.paymentms.service;

import org.springframework.stereotype.Service;
import pl.aplazuk.paymentms.dto.OrderDTO;
import pl.aplazuk.paymentms.model.PaymentStatus;


@Service
public class PaymentService {

    public OrderDTO checkPaymentStatusForGivenOrder(Long orderId, String paymentMethod) {
       return new OrderDTO(orderId, PaymentStatus.getRandomStatus(), paymentMethod);
    }
}
