package pl.aplazuk.orderms.service;

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
import pl.aplazuk.orderms.model.Order;
import pl.aplazuk.orderms.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.*;

@Service
public class OrderService {

    private static final Logger logger = LoggerFactory.getLogger(OrderService.class);
    private static final Random RANDOM = new Random();

    private final OrderRepository orderRepository;
    private final RestClient.Builder restClient;

    public OrderService(OrderRepository orderRepository, RestClient.Builder restClient) {
        this.orderRepository = orderRepository;
        this.restClient = restClient;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Optional<OrderDTO> collectOrderByProductIdAndCategory(String category, Set<Long> productIds) {
        List<ProductDTO> productDTOListByIdsAndCategory = getProductListByIdAndCategory(category, productIds);

        OrderDTO orderDTO = null;
        if (!productDTOListByIdsAndCategory.isEmpty()) {
            orderDTO = new OrderDTO();
            orderDTO.setOrderNumber(RANDOM.nextLong(100));
            orderDTO.setCustomerName("Customer");
            orderDTO.setCustomerId(RANDOM.nextLong(1000));


            orderDTO.setQuantity(productDTOListByIdsAndCategory.size());
            orderDTO.setProducts(mapCategoryToProduct(productDTOListByIdsAndCategory, category));

            BigDecimal totalPrice = productDTOListByIdsAndCategory.stream()
                    .map(ProductDTO::getPrice)
                    .filter(Objects::nonNull)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            orderDTO.setTotalPrice(totalPrice);
            createOrder(orderDTO);
        }

        return Optional.ofNullable(orderDTO);
    }

    public Optional<OrderDTO> checkPaymentStatusForOrderById(Long orderId, String paymentMethod) {
        try {
            OrderDTO orderDTO = restClient.build()
                    .get()
                    .uri("http://localhost:8080/api/payment/check?orderId={orderId}&paymentMethod={paymentMethod}", orderId, paymentMethod)
                    .retrieve()
                    .onStatus(HttpStatusCode::is4xxClientError, (request, response) -> {
                        if (response.getStatusCode().value() == 404) {
                            throw new NoOrderFoundException(response.getStatusText());
                        }
                        throw new HttpClientErrorException(response.getStatusCode(), response.getStatusText());
                    })
                    .body(OrderDTO.class);
            return Optional.ofNullable(orderDTO);
        } catch (NoOrderFoundException e) {
            logger.warn("No orders found for given paymentMethod: {} and orderId: {}", paymentMethod, orderId, e);
            return Optional.empty();
        }
    }

    private List<ProductDTO> getProductListByIdAndCategory(String category, Set<Long> productIds) {
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
                                    throw new NoProductsFoundException(response.getStatusText());
                                }
                                throw new HttpClientErrorException(response.getStatusCode(), response.getStatusText());
                            }
                    )
                    .body(new ParameterizedTypeReference<List<ProductDTO>>() {
                    });
            return body;
        } catch (NoProductsFoundException e) {
            logger.warn("No products found for given category: {}", category, e);
            return Collections.emptyList();
        }
    }

    private List<ProductDTO> mapCategoryToProduct(List<ProductDTO> productDTOListByIdsAndCategory, String category) {
        productDTOListByIdsAndCategory.forEach(productDTO -> productDTO.setCategory(category));
        return productDTOListByIdsAndCategory;
    }

    private void createOrder(OrderDTO orderDTO) {
        if (orderDTO != null) {
            Order order = convertOrderDTOtoOrder(orderDTO);
            order.setStatus("OPEN");
            orderRepository.save(order);
        }
    }

    private Order convertOrderDTOtoOrder(OrderDTO orderDTO) {
        Order order = new Order();
        order.setOrderNumber(orderDTO.getOrderNumber());
        order.setCustomerName(orderDTO.getCustomerName());
        order.setCustomerId(orderDTO.getCustomerId());
        order.setTotalPrice(orderDTO.getTotalPrice());
        return order;
    }
}
