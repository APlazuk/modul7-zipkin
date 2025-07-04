package pl.aplazuk.orderms.config;

import brave.Span;
import brave.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.client.RestClient;

@Configuration
public class OrderConfig {

    Logger logger = LoggerFactory.getLogger(OrderConfig.class);

    private final ApplicationEventPublisher eventPublisher;
    private final Tracer tracer;

    public OrderConfig(ApplicationEventPublisher eventPublisher, Tracer tracer) {
        this.eventPublisher = eventPublisher;
        this.tracer = tracer;
    }

    @Bean
    public RestClient getRestClientForZipkin() {
        return RestClient.builder().baseUrl("http://localhost:8080")
                .requestInterceptor((request, body, execution) -> {
                    Span currentSpan = tracer.currentSpan();
                    if (currentSpan != null) {
                        request.getHeaders().add("X-B3-TraceId", currentSpan.context().traceIdString());
                        request.getHeaders().add("X-B3-SpanId", currentSpan.context().spanIdString());
                        request.getHeaders().add("X-B3-Sampled", "1");
                    }
                    return execution.execute(request, body);
                }).build();
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    public void updateConfigServerProperties() {
        RefreshEvent refreshEvent = new RefreshEvent(this, "RefreshEvent", "Refreshing properties from Config Server");
        eventPublisher.publishEvent(refreshEvent);
        logger.info("Config Server properties updated: {}", refreshEvent.getEventDesc());
    }
}
