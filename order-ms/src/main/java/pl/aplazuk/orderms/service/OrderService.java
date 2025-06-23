package pl.aplazuk.orderms.service;

import brave.Span;
import brave.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import pl.aplazuk.orderms.dto.OrderDTO;
import pl.aplazuk.orderms.dto.ProductDTO;
import pl.aplazuk.orderms.mapper.OrderMapper;
import pl.aplazuk.orderms.model.Order;
import pl.aplazuk.orderms.repository.OrderRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private static final Random RANDOM = new Random();

    private final OrderRepository orderRepository;
    private final RestClient.Builder restClient;
    private final Tracer tracer;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, RestClient.Builder restClient, Tracer tracer, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.restClient = restClient;
        this.tracer = tracer;
        this.orderMapper = orderMapper;
    }

    public List<OrderDTO> getAllOrders() {
        return orderMapper.toOrderDTOs(orderRepository.findAll());
    }

    public Optional<OrderDTO> collectOrderByProductIdAndCategory(String category, Set<Long> productIds) {
        Span span = tracer.nextSpan().name("order-collect-processing").tag("category", category);
        span.start();
        try {
            List<ProductDTO> productDTOListByIdsAndCategory = getProductListByIdAndCategory(category, productIds);
            OrderDTO orderDTO = null;
            if (!productDTOListByIdsAndCategory.isEmpty()) {
                orderDTO = createOrder(category, productDTOListByIdsAndCategory);
                saveOrder(orderDTO);
            }

            return Optional.ofNullable(orderDTO);
        } finally {
            span.finish();
        }
    }

    public Optional<OrderDTO> checkPaymentStatusForOrderById(Long orderId, String paymentMethod) {
        Span span = tracer.nextSpan().name("order-payment-call").tag("order", String.valueOf(orderId));
        span.start();
        try {
            OrderDTO orderDTO = restClient.build()
                    .get()
                    .uri("http://localhost:8080/api/payment/check?orderId={orderId}&paymentMethod={paymentMethod}", orderId, paymentMethod)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            String responseBody = readResponse(response.getBody());
                            throw new NoOrderFoundException(response.getStatusText(), responseBody);
                        }
                        throw new HttpClientErrorException(response.getStatusCode(), response.getStatusText());
                    })
                    .body(OrderDTO.class);
            return Optional.ofNullable(orderDTO);
        } finally {
            span.finish();
        }
    }

    private List<ProductDTO> getProductListByIdAndCategory(String category, Set<Long> productIds) {
        Span span = tracer.nextSpan().name("order-inventory-call").tag("category", category);
        span.start();
        try {
            List<ProductDTO> body = restClient.build()
                    .get()
                    .uri(UriComponentsBuilder.fromUriString("http://localhost:8080/api/inventory/check")
                            .queryParam("category", category)
                            .queryParam("productIds", productIds)
                            .build()
                            .toUri())
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                                if (response.getStatusCode().value() == 404) {
                                    String responseBody = readResponse(response.getBody());
                                    throw new NoProductsFoundException(response.getStatusText(), responseBody);
                                }
                                throw new HttpClientErrorException(response.getStatusCode(), response.getStatusText());
                            }
                    )
                    .body(new ParameterizedTypeReference<List<ProductDTO>>() {
                    });
            return body;
        } finally {
            span.finish();
        }
    }

    private OrderDTO createOrder(String category, List<ProductDTO> productDTOListByIdsAndCategory) {
        Span span = tracer.nextSpan().name("order-create-processing").tag("category", category);
        span.start();
        try {
            OrderDTO orderDTO = new OrderDTO();
            orderDTO.setOrderNumber(RANDOM.nextLong(100));
            orderDTO.setCustomerName("Customer");
            orderDTO.setCustomerId(RANDOM.nextLong(1000));
            return orderMapper.toOrderDTO(orderDTO, productDTOListByIdsAndCategory, category);
        } finally {
            span.finish();
        }
    }

    private void saveOrder(OrderDTO orderDTO) {
        if (orderDTO != null) {
            Span dbSpan = tracer.newTrace().name("order-save-db").tag("order", orderDTO.getOrderNumber());
            try {
                Order order = orderMapper.toOrder(orderDTO);
                order.setStatus("OPEN");

                dbSpan.start();
                orderRepository.save(order);
            } finally {
                dbSpan.finish();
            }
        }
    }

    private static String readResponse(InputStream body) throws IOException {
        try (body) {
            if (body != null) {
                return new String(body.readAllBytes());
            }
        } catch (IOException ex) {
            throw new IOException(ex);
        }
        return "[unreadable body]";
    }
}
