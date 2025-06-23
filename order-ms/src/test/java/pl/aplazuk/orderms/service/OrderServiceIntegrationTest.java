package pl.aplazuk.orderms.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;
import pl.aplazuk.orderms.dto.OrderDTO;
import pl.aplazuk.orderms.dto.ProductDTO;
import pl.aplazuk.orderms.model.Order;
import pl.aplazuk.orderms.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class OrderServiceIntegrationTest {

    private static final String CATEGORY = "toys";


    private MockRestServiceServer mockServer;

    @MockitoBean
    private OrderRepository orderRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private RestClient.Builder restClient;


    @Captor
    ArgumentCaptor<Order> orderCaptor;

    private List<ProductDTO> productsByCategory;
    private Set<Long> mockProductIds;

    @BeforeEach
    void setUp() {
        mockServer = MockRestServiceServer.bindTo(restClient).build();
    }


    @Test
    public void shouldCallApiAndReturnSelectedProductsByCategory() throws JsonProcessingException {
        //given
        mockProductIds = Set.of(1L, 2L, 3L, 4L);

        //when
        Optional<OrderDTO> actual = orderService.collectOrderByProductIdAndCategory(CATEGORY, mockProductIds);

        //then
        verify(orderRepository, times(1)).save(orderCaptor.capture());
        assertTrue(actual.isPresent());
        assertEquals(2, actual.get().getProducts().size());
        assertEquals(new BigDecimal("576.00"), actual.get().getTotalPrice());

        mockServer.verify();
    }

    @Test
    public void shouldCallApiAndNotReturnOrderWithSelectedProductsByCategory() {
        //given
        mockProductIds = Set.of(1L);

        //when
        NoProductsFoundException exception = assertThrows(NoProductsFoundException.class, () -> {
                    orderService.collectOrderByProductIdAndCategory(CATEGORY, mockProductIds);
                }
        );

        //then
        verify(orderRepository, never()).save(orderCaptor.capture());
        assertTrue(exception.getResponseBody().contains("No products found in inventory for category"));

        mockServer.verify();
    }

}