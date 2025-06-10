package pl.aplazuk.paymentms.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import pl.aplazuk.paymentms.model.PaymentStatus;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderDTO(Long orderId,
                       PaymentStatus status,
                       String paymentMethod) {
}