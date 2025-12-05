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
            // 1. CORS Configuration (CRITICAL for filter chain ordering)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            
            // CSRF must be disabled for cross-site cookie-based communication
            .csrf(csrf -> csrf.disable())

            // 2. Authorization Rules (Ordered from most specific to general)
            .authorizeHttpRequests(auth -> auth
                
                // Allow OPTIONS pre-flight requests globally (must be first!)
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                
                // Public/Unsecured endpoints
                .requestMatchers("/error", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/createUser").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/forgot-password").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/forgot-username").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/users/reset-password").permitAll()
                
                // Authenticated Endpoint Grouping (All other requests must be logged in)
                .requestMatchers("/api/admin/**").authenticated() 
                .requestMatchers("/api/users/**").authenticated() 
                .requestMatchers("/api/incidents/**").authenticated()
                .requestMatchers("/api/workgroups/**").authenticated()
                .requestMatchers("/api/roles/**").authenticated()
                .requestMatchers("/api/resolution-codes/**").authenticated()
                .requestMatchers("/api/pending-reasons/**").authenticated()
                .requestMatchers("/api/config/**").authenticated()
                .requestMatchers("/api/impacts/**").authenticated()
                .requestMatchers("/api/classifications/**").authenticated()
                .requestMatchers("/api/assignment-groups/**").authenticated()

                // Default fallback: any other request must be authenticated
                .anyRequest().authenticated()
            )
            
            // 3. Session Management
            .sessionManagement(sess -> sess
                // Use session as required by JSESSIONID
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) 
            )
            
            // Disable default authentication mechanisms not being used
            .formLogin(form -> form.disable())
            .httpBasic(httpBasic -> httpBasic.disable())
            .anonymous(anon -> anon.disable())

            // Exception Handling: Send 401 for unauthenticated users
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(401, "Unauthorized: Please login first");
                })
            );

        return http.build();
    }

    // ----------------------------------------------------------------------
    // CORS Configuration Bean: Centralized and Secured
    // ----------------------------------------------------------------------

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 🔑 CRITICAL: List your frontend origins (Vercel and local)
        config.setAllowedOrigins(List.of(
            "https://incident-management-frontend.vercel.app", 
            "http://127.0.0.1:5173", 
            "http://localhost:3000"
        ));

        // Allow all necessary methods for both preflight and actual requests
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        
        // Allow all headers
        config.setAllowedHeaders(List.of("*"));
        
        // 🔑 CRITICAL: This allows the browser to send the JSESSIONID cookie cross-origin
        config.setAllowCredentials(true); 

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Apply this configuration to ALL paths
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}