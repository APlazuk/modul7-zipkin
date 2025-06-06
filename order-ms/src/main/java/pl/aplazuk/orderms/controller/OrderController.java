package pl.aplazuk.orderms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.aplazuk.orderms.dto.OrderDTO;
import pl.aplazuk.orderms.model.Order;
import pl.aplazuk.orderms.service.OrderService;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/api/order")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(orderService.getAllOrders());
    }

    @GetMapping("/collect/{category}")
    public ResponseEntity<?> getOrderWithProductsListByCategory(@PathVariable(name = "category") String productCategory, @RequestParam Set<UUID> productIds) {
        Optional<OrderDTO> orderDTO = orderService.collectOrderByProductIdAndCategory(productCategory, productIds);
        return orderDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());

    }

    @GetMapping("/payment/status")
    public ResponseEntity<Order> getPaymentStatusByOrderId(@RequestParam Long orderId, @RequestParam String paymentMethod) {
        orderService.checkPaymentStatusForOrder(orderId, paymentMethod);
    }

}
