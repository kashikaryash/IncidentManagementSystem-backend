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

		http.cors(cors -> cors.configurationSource(corsConfigurationSource()))

				.csrf(csrf -> csrf.disable())

				.authorizeHttpRequests(auth -> auth

						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

						.requestMatchers("/error", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/users/login").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/users/createUser").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/users/forgot-password").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/users/forgot-username").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/users/reset-password").permitAll()
						.requestMatchers("/api/admin/**").authenticated().requestMatchers("/api/users/**")
						.authenticated().requestMatchers("/api/incidents/**").authenticated()
						.requestMatchers("/api/workgroups/**").authenticated().requestMatchers("/api/roles/**")
						.authenticated().requestMatchers("/api/resolution-codes/**").authenticated()
						.requestMatchers("/api/pending-reasons/**").authenticated().requestMatchers("/api/config/**")
						.authenticated().requestMatchers("/api/impacts/**").authenticated()
						.requestMatchers("/api/classifications/**").authenticated()
						.requestMatchers("/api/assignment-groups/**").authenticated()

						.anyRequest().authenticated())

				.sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED))

				.formLogin(form -> form.disable()).httpBasic(httpBasic -> httpBasic.disable())
				.anonymous(anon -> anon.disable())

				.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
					response.sendError(401, "Unauthorized: Please login first");
				}));

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration config = new CorsConfiguration();

		config.setAllowedOrigins(List.of("https://incident-management-frontend.vercel.app", "http://127.0.0.1:5173",
				"http://localhost:3000"));

		config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));

		config.setAllowedHeaders(List.of("*"));

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