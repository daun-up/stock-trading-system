package com.toybroker.trading.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI stockTradingOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Stock Trading System API")
                        .version("v1")
                        .description("Order, account balance, and system APIs for the stock trading system."));
    }
}
