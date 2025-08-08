package com.aisentinel.security.config;

import com.aisentinel.security.interceptor.AiSecurityInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class for Spring Security and Web MVC Interceptor.
 * This class defines how HTTP requests are secured and where our AI security interceptor is applied.
 */
@Configuration
@EnableWebSecurity // Enables Spring Security's web security support
public class SecurityConfig implements WebMvcConfigurer { // Implements WebMvcConfigurer to add custom interceptors

    private final AiSecurityInterceptor aiSecurityInterceptor;

    // Inject our custom AI security interceptor
    public SecurityConfig(AiSecurityInterceptor aiSecurityInterceptor) {
        this.aiSecurityInterceptor = aiSecurityInterceptor;
    }

    /**
     * Defines the security filter chain for HTTP requests.
     * This method configures authentication and authorization rules.
     * @param http HttpSecurity object to configure security settings.
     * @return A SecurityFilterChain instance.
     * @throws Exception if configuration fails.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for simplicity in this example. In production, consider enabling.
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public/**").permitAll() // Allow public access to paths starting with /public/
                .anyRequest().authenticated() // All other requests require authentication
            )
            .httpBasic(org.springframework.security.config.Customizer.withDefaults()); // Enable HTTP Basic authentication

        return http.build();
    }

    /**
     * Configures an in-memory user details service for demonstration purposes.
     * In a real application, this would be replaced with a database-backed or external identity provider.
     * @return An InMemoryUserDetailsManager instance.
     */
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder.encode("password")) // Encode the password
            .roles("USER") // Assign a role to the user
            .build();
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("adminpass"))
            .roles("ADMIN", "USER") // Assign multiple roles
            .build();
        return new InMemoryUserDetailsManager(user, admin);
    }

    /**
     * Provides a BCrypt password encoder for secure password hashing.
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Adds our custom AI security interceptor to the Spring MVC interceptor registry.
     * This ensures that our AiSecurityInterceptor is executed for every incoming request.
     * @param registry InterceptorRegistry to add interceptors to.
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // Add our AI security interceptor to all paths.
        // This makes sure our AI Guardian is active for all incoming requests.
        registry.addInterceptor(aiSecurityInterceptor).addPathPatterns("/**");
    }
}