package pl.aplazuk.orderms.service;

import brave.Tracer;
import brave.Tracing;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import pl.aplazuk.orderms.dto.OrderDTO;
import pl.aplazuk.orderms.dto.ProductDTO;
import pl.aplazuk.orderms.mapper.OrderMapper;
import pl.aplazuk.orderms.model.Order;
import pl.aplazuk.orderms.repository.OrderRepository;

import java.math.BigDecimal;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    private static final String CATEGORY = "zabawki";

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private RestClient restClient;

    @Mock
    private RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    @Mock
    private RestClient.ResponseSpec responseSpec;

    @Mock
    private RestClient.Builder restClientBuilder;

    private Tracing braveTracing;
    private Tracer tracer;

    private OrderService orderService;
    private OrderMapper orderMapper;

    private Set<Long> mockProductIds;
    private List<ProductDTO> mockProductsByCategory;

    @Captor
    ArgumentCaptor<Order> orderCaptor;

    @BeforeEach
    void setUp() {
        orderMapper = Mappers.getMapper(OrderMapper.class);
        braveTracing = Tracing.newBuilder().build();
        tracer = braveTracing.tracer();

        orderService = new OrderService(orderRepository, restClientBuilder, tracer, orderMapper);

        when(restClientBuilder.build()).thenReturn(restClient);
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);

        List<ProductDTO> products = List.of(
                new ProductDTO(1L, "samochód zdalnie sterowany", "zabawki", new BigDecimal("12.55")),
                new ProductDTO(2L, "klocki lego", "zabawki", new BigDecimal("48.89")),
                new ProductDTO(3L, "poradnik jak pisać testy jednostkowe", "książki", new BigDecimal("25.00"))
        );
        mockProductIds = products.stream().map(ProductDTO::getId).collect(Collectors.toSet());
        mockProductsByCategory = products.stream().filter(product -> product.getCategory().equals(CATEGORY)).toList();
    }

    @Test
    public void shouldReturnSelectedProductsByCategory() {
        //given
        BigDecimal totalPriceForProductByCategory = mockProductsByCategory.stream().map(ProductDTO::getPrice).reduce(BigDecimal.ZERO, BigDecimal::add);
        when(responseSpec.body(new ParameterizedTypeReference<List<ProductDTO>>() {
        })).thenReturn(mockProductsByCategory);
        when(responseSpec.onStatus(any(), any())).thenReturn(responseSpec);

        //when
        Optional<OrderDTO> actual = orderService.collectOrderByProductIdAndCategory(CATEGORY, mockProductIds);

        //then
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        assertTrue(actual.isPresent());
        assertEquals(2, actual.get().getProducts().size());
        assertEquals(totalPriceForProductByCategory, actual.get().getTotalPrice());
    }

    @Test
    public void shouldNotReturnOrderWithSelectedProductsByCategory() {
        //given
        ArgumentCaptor<Predicate<HttpStatusCode>> statusCodeCaptor = ArgumentCaptor.forClass(Predicate.class);
        when(responseSpec.onStatus(any(Predicate.class), any(RestClient.ResponseSpec.ErrorHandler.class))).thenReturn(responseSpec);
        when(responseSpec.body(new ParameterizedTypeReference<List<ProductDTO>>() {
        })).thenThrow(new NoProductsFoundException(HttpStatus.NOT_FOUND.getReasonPhrase(), "No products found for given category: " + CATEGORY));

        //when
        NoProductsFoundException exception = assertThrows(NoProductsFoundException.class, () -> {
                    orderService.collectOrderByProductIdAndCategory(CATEGORY, mockProductIds);
                }
        );

        //then
        verify(orderRepository, never()).save(orderCaptor.capture());
        verify(responseSpec, times(1)).onStatus(statusCodeCaptor.capture(), any());
        assertTrue(statusCodeCaptor.getValue().test(HttpStatus.NOT_FOUND));
        assertEquals("No products found for given category: " + CATEGORY, exception.getResponseBody());
    }

}