package io.getarrays.userservice.security;

import io.getarrays.userservice.filter.CustomAuthorizationFilter;
import io.getarrays.userservice.filter.MyAuthenticationFilter;
import io.getarrays.userservice.service.RateLimiterService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * @author VM-frescent
 * @version 1.0
 * @since 07/01/2026
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    // Spring akan otomatis mengambil UserServiceImpl-mu karena ada @Service
    private final UserDetailsService userDetailsService;
    // Spring akan mengambil Bean dari Application Class di atas
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RateLimiterService rateLimiterService;

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authManager) throws Exception {
        // Buat filter masukkan manager lewat constructor
        MyAuthenticationFilter customFilter = new MyAuthenticationFilter(authManager);
        // set URL login di sini agar tidak default /login
        customFilter.setFilterProcessesUrl("/api/login");

        http.csrf(AbstractHttpConfigurer::disable); // http.csrf().disable()
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/login/**", "/api/token/refresh/**").permitAll() // Sesuai baris 4 di gambar
                .requestMatchers(HttpMethod.GET, "/api/user/**").hasAnyAuthority("ROLE_USER") // Sesuai baris 5
                .requestMatchers(HttpMethod.POST, "/api/user/save/**").hasAnyAuthority("ROLE_ADMIN") // Sesuai baris 6
                .anyRequest().authenticated() // Sesuai baris 7
        );

        // Tambahkan filter kustom ke dalam barisan filter
        http.addFilter(customFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(rateLimiterService), UsernamePasswordAuthenticationFilter.class);

        return http.build();

    }
}

