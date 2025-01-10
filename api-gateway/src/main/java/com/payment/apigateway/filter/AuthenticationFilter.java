package com.payment.apigateway.filter;


import com.payment.apigateway.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationFilter implements GlobalFilter {

    @Autowired
    private RouteValidator validator;

    @Autowired
    private JwtUtil jwtUtil;


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Pre-processing: Log request details
        System.out.println("Intercepted Request Path: " + exchange.getRequest().getPath());
        System.out.println("Intercepted Request Headers: " + exchange.getRequest().getHeaders());

        // Modify the request (e.g., add a custom header)
        exchange.getRequest().mutate().header("X-Custom-Header", "Intercepted").build();
        if (validator.isSecured.test(exchange.getRequest())) {
            //header contains token or not
            if (!exchange.getRequest().getHeaders().containsKey(HttpHeaders.AUTHORIZATION)) {
                exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                return exchange.getResponse().setComplete();
            }

            String authHeader = exchange.getRequest().getHeaders().get(HttpHeaders.AUTHORIZATION).get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                authHeader = authHeader.substring(7);
            }
            try {
                jwtUtil.validateToken(authHeader);

            } catch (Exception e) {
                System.out.println("invalid access...!");
                throw new RuntimeException("un authorized access to application");
            }
        }
        // Post-processing: Log response details after the request is handled
        return chain.filter(exchange).then(Mono.fromRunnable(() -> {
            System.out.println("Response Status Code: " + exchange.getResponse().getStatusCode());
        }));
    }




}
