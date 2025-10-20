package com.example.demo.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.example.demo.security.CustomAuthenticationFailureHandler;
import com.example.demo.security.CustomAuthenticationSuccessHandler;
import com.example.demo.security.UserDetailsServiceImpl;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

        @Autowired
        private UserDetailsServiceImpl userDetailsService;

        @Autowired
        private CustomAuthenticationSuccessHandler authenticationSuccessHandler;

        @Autowired
        private CustomAuthenticationFailureHandler authenticationFailureHandler;

        @Bean
        public PasswordEncoder passwordEncoder() {
                return new BCryptPasswordEncoder();
        }

        @Bean
        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
                http
                                .authorizeHttpRequests(authz -> authz
                                                // Permit access to login page and resources needed to render it
                                                .requestMatchers("/login", "/css/login.css", "/favicon.ico").permitAll()
                                                .requestMatchers("/admin/**").hasRole("ADMIN")
                                                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                                                .requestMatchers("/api/batch/**").hasAnyRole("USER", "ADMIN")
                                                .anyRequest()
                                                .authenticated())
                                .formLogin(form -> form
                                                .loginPage("/login")
                                                .successHandler(authenticationSuccessHandler)
                                                .failureHandler(authenticationFailureHandler)
                                                .permitAll())
                                .logout(logout -> logout
                                                .logoutSuccessUrl("/login?logout")
                                                .permitAll())
                                .userDetailsService(userDetailsService);

                return http.build();
        }

}
