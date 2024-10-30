package com.rsuniverse.jobify_gateway.config.filter;

import com.rsuniverse.jobify_gateway.models.pojos.AuthUser;
import com.rsuniverse.jobify_gateway.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Configuration
public class JwtFilter implements GatewayFilter {

    private static final List<String> WHITELISTED_URLS = List.of("/auth/**");
    private static final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        String token = extractJwtFromRequest(exchange.getRequest());

        boolean isWhitelisted = WHITELISTED_URLS.stream()
                .anyMatch(pattern -> pathMatcher.match(pattern, path));

        if (isWhitelisted) {
            return chain.filter(exchange);
        } else if ((token != null && JwtUtils.validateToken(token, "jobify:user_access_token"))) {

            Map<String, Object> claims = JwtUtils.getClaims(token);


            Map<String, String> fieldMap = makePayload(claims);
            log.info(fieldMap.toString());
            ServerHttpRequest.Builder requestBuilder = exchange.getRequest().mutate();
            fieldMap.forEach(requestBuilder::header);
            ServerHttpRequest modifiedRequest = requestBuilder.build();

            return chain.filter(exchange.mutate().request(modifiedRequest).build());
        } else {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            return exchange.getResponse().setComplete();
        }
    }

    private String extractJwtFromRequest(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null; // Return null if the token is invalid
    }
    public Map<String, String> makePayload(Map<String, Object> claims) {
        Map<String, String> fieldMap = new HashMap<>();

        for (Map.Entry<String, Object> entry : claims.entrySet()) {
            Object value = entry.getValue();

            if (value != null) {
                if (value instanceof String) {
                    fieldMap.put(entry.getKey(), (String) value);
                } else if (value instanceof Set<?>) {
                    String joinedValues = ((Set<?>) value).stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(","));
                    fieldMap.put(entry.getKey(), joinedValues);
                } else if (value instanceof List<?>) {
                    String joinedValues = ((List<?>) value).stream()
                            .map(Object::toString)
                            .collect(Collectors.joining(","));
                    fieldMap.put(entry.getKey(), joinedValues);
                } else {
                    fieldMap.put(entry.getKey(), value.toString());
                }
            }
        }
        return fieldMap;
    }

}
