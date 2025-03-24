package com.example.API_GATEWAY.filters;

import java.time.Duration;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.web.server.ServerWebExchange;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import reactor.core.publisher.Mono;

public class RateLimiterFilter implements GlobalFilter,Ordered {
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket(){
        return Bucket.builder()
        .addLimit(Bandwidth.classic(10, Refill.greedy(10, Duration.ofMinutes(1))))
        .build();
    }

    @Override
    public int getOrder() {
        return -1;
    }
    @NonNull
    @Override
    public Mono<Void> filter(ServerWebExchange exchange,  GatewayFilterChain chain) {
    String ip = Optional.ofNullable(exchange.getRequest())
                .map(request -> request.getRemoteAddress())
                .map(remoteAddress -> remoteAddress.getAddress())
                .map(address -> address.getHostAddress())
                .orElse("UNKNOWN");

        Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());

        if(bucket.tryConsume(1)){
            return chain.filter(exchange);
        }
        else {
            exchange.getResponse().setStatusCode(HttpStatus.TOO_MANY_REQUESTS);
            return exchange.getResponse().setComplete();
        }
    }
    
}
