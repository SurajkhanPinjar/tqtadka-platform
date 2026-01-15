package com.tqtadka.platform.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // ================= AUTHORIZATION =================
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/en/login",
                                "/en/signup",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**"
                        ).permitAll()

                        // 🔐 ADMIN ONLY
                        .requestMatchers("/admin/**").hasRole("ADMIN")

                        .anyRequest().permitAll()
                )

                // ================= LOGIN =================
                .formLogin(form -> form
                        .loginPage("/en/login")
                        .loginProcessingUrl("/login")
                        .successHandler(authenticationSuccessHandler()) // 🔥 FIX
                        .permitAll()
                )

                // ================= LOGOUT =================
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .permitAll()
                );

        return http.build();
    }

    // ================= ROLE BASED REDIRECT =================
    @Bean
    public AuthenticationSuccessHandler authenticationSuccessHandler() {
        return (request, response, authentication) -> {

            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                response.sendRedirect("/admin/posts");
            } else {
                response.sendRedirect("/");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}