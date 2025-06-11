package pl.aplazuk.apigateway;

import io.micrometer.observation.Observation;
import io.micrometer.observation.ObservationFilter;
import org.springframework.cloud.gateway.route.Route;
import org.springframework.cloud.gateway.support.ServerWebExchangeUtils;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.observation.ServerRequestObservationContext;

@Configuration
public class ServerRequestObservationFilter implements ObservationFilter {

    //source: https://docs.spring.io/spring-framework/reference/integration/observability.html
    @Override
    public Observation.Context map(Observation.Context context) {
        if (context instanceof ServerRequestObservationContext serverContext){
            // fetching data from gateway
            Route route = (Route) serverContext.getCarrier().getAttributes().get(ServerWebExchangeUtils.GATEWAY_ROUTE_ATTR);
            String routeId = route.getId();
            String method = serverContext.getCarrier().getMethod().name().toLowerCase();
            String path = String.valueOf(serverContext.getCarrier().getPath());

            String customName = String.format(
                    "http %s %s %s",
                    method, routeId.toLowerCase(), path
            );

            //override Metrics and Tracing span name
            context.setName(customName);
            context.setContextualName(customName);
        }
        return context;
    }
}
