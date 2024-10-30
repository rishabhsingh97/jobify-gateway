package com.rsuniverse.jobify_gateway.config;

import com.rsuniverse.jobify_gateway.config.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                .route("user_service", r -> r
                        .path("/users/**", "/auth/**", "/job-seekers/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://jobify-user"))

                .route("job_service", r -> r
                        .path("/jobs/**", "/applications/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://jobify-job"))

                .route("analytics_service", r -> r
                        .path("/analytics/**", "/reports/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://jobify-analytics"))

                .route("notification_service", r -> r
                        .path("/notifications/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://jobify-notification"))

                .route("rss_service", r -> r
                        .path("/news/**", "/rss-feeds/**")
                        .filters(f -> f.filter(jwtFilter))
                        .uri("lb://jobify-rss"))
                .build();
    }
}
