package pl.aplazuk.inventoryms.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

@Configuration
public class InventoryConfig {

    Logger logger = LoggerFactory.getLogger(InventoryConfig.class);

    private final ApplicationEventPublisher eventPublisher;

    public InventoryConfig(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    @Scheduled(cron = "0 0/15 * * * ?")
    public void updateConfigServerProperties() {
        RefreshEvent refreshEvent = new RefreshEvent(this, "RefreshEvent", "Refreshing properties from Config Server");
        eventPublisher.publishEvent(refreshEvent);
        logger.info("Config Server properties updated: {}", refreshEvent.getEventDesc());
    }
}
