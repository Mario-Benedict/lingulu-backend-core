package com.lingulu.config;

import com.lingulu.security.JwtAuthenticationEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import com.lingulu.security.JwtAuthenticationFilter;
import com.lingulu.security.OAuth2SuccessHandler;
import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final JwtAuthenticationEntryPoint entryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // API ONLY
                .csrf(csrf -> csrf.disable())

                // WAJIB STATELESS
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // UNAUTHORIZED HANDLER
                .exceptionHandling(ex ->
                        ex.authenticationEntryPoint(entryPoint)
                )

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/api/account/register",
                                "/api/account/login",
                                "/login/oauth2/**",
                                "/oauth2/**"
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                .oauth2Login(oauth ->
                        oauth.successHandler(oAuth2SuccessHandler)
                )

                .formLogin(form -> form.disable())

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(
                Arrays.asList("http://localhost:5173")
        );
        configuration.setAllowedMethods(
                Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );
        configuration.setAllowedHeaders(
                Arrays.asList("Authorization", "Content-Type")
        );
        configuration.setAllowCredentials(true); // karena pakai cookie
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
