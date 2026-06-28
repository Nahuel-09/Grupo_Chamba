package com.grupochamba.sz53.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
        .csrf(csrf -> csrf.disable()) 
        .authorizeHttpRequests(auth -> auth

            
            .requestMatchers("/webjars/**", "/css/**", "/js/**", "/images/**").permitAll()
            .requestMatchers("/", "/home").authenticated()
            .requestMatchers("/usuarios/**").hasRole("ADMIN")
            .requestMatchers("/categorias/**").hasAnyRole("ADMIN", "STOCK")
            .requestMatchers(org.springframework.http.HttpMethod.GET, "/productos", "/productos/").hasAnyRole("ADMIN", "STOCK", "VENDEDOR")
            .requestMatchers("/productos/**").hasAnyRole("ADMIN", "STOCK") 
            .requestMatchers("/clientes/**").hasAnyRole("ADMIN", "VENDEDOR")
            .requestMatchers("/notas/**").hasAnyRole("ADMIN", "STOCK")
            .requestMatchers("/ventas/**").hasAnyRole("ADMIN", "VENDEDOR")
            .requestMatchers("/reportes/**").hasAnyRole("ADMIN", "STOCK", "VENDEDOR")
            .anyRequest().authenticated()
        )
        .formLogin(form -> form
            .loginPage("/login")               
            .loginProcessingUrl("/login")       
            .defaultSuccessUrl("/", true)       
            .failureUrl("/login?error=true")   
            .permitAll()
        )
        .logout(logout -> logout
            .logoutSuccessUrl("/login?logout")
            .permitAll()
        );

    return http.build();
    }
}
