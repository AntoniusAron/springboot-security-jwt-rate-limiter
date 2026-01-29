package io.getarrays.userservice.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.interfaces.DecodedJWT;
import io.getarrays.userservice.service.RateLimiterService;
import io.getarrays.userservice.utility.JwtUtils;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

/**
 * @author VM-frescent
 * @version 1.0
 * @since 21/01/2026
 */
@Slf4j
public class CustomAuthorizationFilter extends OncePerRequestFilter {
    // RateLimiterService
    private final RateLimiterService rateLimiterService;

    // RateLimiterService di-inject
    public CustomAuthorizationFilter(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // Pakai IP Address sebagai kunci (Key)
        String ipAddr = request.getRemoteAddr();
        Bucket bucket = rateLimiterService.resolveBucket(ipAddr);

        if (!bucket.tryConsume(1)) {
            log.warn("Rate limit exceeded for IP: {}", ipAddr);
            response.setStatus(429); // Too Many Requests
            response.setContentType(APPLICATION_JSON_VALUE);
            Map<String, String> errorMap = new HashMap<>();
            errorMap.put("error_message", "Limit 3 requests per minute.");
            new ObjectMapper().writeValue(response.getOutputStream(), errorMap);
            return; //  lanjut ke filterChain
        }

        if(request.getServletPath().equals("/api/login") || request.getServletPath().equals("/api/token/refresh")) {
            filterChain.doFilter(request, response);
        } else {
            String authorizationHeader = request.getHeader(AUTHORIZATION);
            if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                try {
                    String token = authorizationHeader.substring("Bearer ".length());
                    JWTVerifier verifier = JWT.require(JwtUtils.ALGORITHM).build();
                    DecodedJWT decodedJWT = verifier.verify(token);
                    String username = decodedJWT.getSubject();
                    String[] roles = decodedJWT.getClaim("roles").asArray(String.class);
                    Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
                    stream(roles).forEach(role ->{
                        authorities.add(new SimpleGrantedAuthority(role));
                    });
                    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                    filterChain.doFilter(request, response);
                } catch (Exception e) {
                    log.error("Error Authorization: {}", e.getMessage());
                    response.setHeader("Error", e.getMessage());
                    response.setStatus(FORBIDDEN.value());
                    Map<String, String> errorMap = new HashMap<>();
                    errorMap.put("error_message", e.getMessage());
                    response.setContentType(APPLICATION_JSON_VALUE);
                    new ObjectMapper().writeValue(response.getOutputStream(), errorMap);
                }
            } else {
                filterChain.doFilter(request, response);
            }
        }
    }
}
