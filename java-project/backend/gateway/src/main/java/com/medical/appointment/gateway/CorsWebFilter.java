package com.medical.appointment.gateway;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class CorsWebFilter implements WebFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = response.getHeaders();
        headers.set("Access-Control-Allow-Origin", "http://localhost:3000");
        headers.set("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        headers.set("Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Origin, Accept");
        headers.set("Access-Control-Allow-Credentials", "true");
        headers.set("Access-Control-Expose-Headers", "*");
        if ("OPTIONS".equalsIgnoreCase(request.getMethodValue())) {
            response.setStatusCode(HttpStatus.OK);
            return response.setComplete();
        }
        return chain.filter(exchange);
    }
}