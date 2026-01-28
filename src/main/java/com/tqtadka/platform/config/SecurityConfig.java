package com.tqtadka.platform.config;

import com.tqtadka.platform.security.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService customUserDetailsService;

    public SecurityConfig(CustomUserDetailsService customUserDetailsService) {
        this.customUserDetailsService = customUserDetailsService;
    }

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                /* ================= AUTHZ ================= */
                .authorizeHttpRequests(auth -> auth

                        /* ---------- PUBLIC ---------- */
                        .requestMatchers(
                                "/",
                                "/en/login",
                                "/en/signup",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/webjars/**"
                        ).permitAll()

                        /* ---------- ADMIN ONLY ---------- */
                        .requestMatchers(
                                "/admin/users/**"
                        ).hasRole("ADMIN")

                        /* ---------- ADMIN + AUTHOR ---------- */
                        .requestMatchers(
                                "/admin/posts/**",
                                "/admin/dashboard/**", "/admin/images/**"
                        ).hasAnyRole("ADMIN", "AUTHOR")

                        /* ---------- EVERYTHING ELSE ---------- */
                        .anyRequest().permitAll()
                )

                /* ================= LOGIN ================= */
                .formLogin(form -> form
                        .loginPage("/en/login")
                        .loginProcessingUrl("/login")
                        .usernameParameter("email")
                        .passwordParameter("password")
                        .defaultSuccessUrl("/admin/posts", true)
                        .failureUrl("/en/login?error")
                )

                /* ================= LOGOUT ================= */
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                )

                /* ================= ERROR ================= */
                .exceptionHandling(ex -> ex
                        .accessDeniedPage("/403")
                );

        return http.build();
    }

    /* ================= AUTH MANAGER ================= */
    @Bean
    public AuthenticationManager authenticationManager(
            HttpSecurity http,
            PasswordEncoder passwordEncoder
    ) throws Exception {

        AuthenticationManagerBuilder builder =
                http.getSharedObject(AuthenticationManagerBuilder.class);

        builder
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder);

        return builder.build();
    }

    /* ================= PASSWORD ================= */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}