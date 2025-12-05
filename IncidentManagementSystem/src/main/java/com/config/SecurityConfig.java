package com.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // 1. CORS Configuration (CRITICAL for Vercel/Railway cross-site)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            // 2. Authorization Rules
            .authorizeHttpRequests(auth -> auth
                // Allow OPTIONS pre-flight requests and public API paths
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .requestMatchers("/error", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                
                // ⭐️ FIX: Explicitly permit only the login and create user POST calls
                .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/createUser").permitAll() 
                
                // ⭐️ FIX: Require authentication for ALL other /api/users/* requests (including /me)
                .requestMatchers("/api/users/**").authenticated() // <-- This now forces session check
                
                // Require authentication for all other protected paths
                .requestMatchers(
                    "/api/config/**",
                    "/api/categories/**",
                    "/api/workgroups/**",
                    "/api/incidents/**",
                    "/api/admin/**",
                    // ... list all other protected endpoints here ...
                    "/api/priorities/**"
                ).authenticated()

                // Default fallback: any other request must be authenticated
                .anyRequest().authenticated()
            )
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            
            // 3. Session Management
            .sessionManagement(sess -> sess
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Use session
            )
            // Send 401 instead of 403 for unauthenticated users
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(401, "Unauthorized: Please login first");
                })
            )
            .anonymous(anon -> anon.disable());

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Ensure the Vercel domain is listed
        config.setAllowedOrigins(List.of(
            "https://incident-management-frontend.vercel.app", 
            "http://127.0.0.1:5173", 
            "http://localhost:3000"
        ));

        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        
        // 🔑 CRITICAL: This allows the browser to send cookies (JSESSIONID)
        config.setAllowCredentials(true); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}