package com.synex.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.synex.service.CustomUserDetailsService;

import jakarta.servlet.DispatcherType;


@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
    @Bean
    public PasswordEncoder passwordEncoder() {
	    return new BCryptPasswordEncoder();
	}
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
            	.dispatcherTypeMatchers(DispatcherType.FORWARD, DispatcherType.ERROR).permitAll()
                .requestMatchers("/login", "/login**", "/h2-console/**").permitAll()
                // Ticket endpoints - accessible by authenticated users
                .requestMatchers("/tickets/my_tickets").hasAnyAuthority("USER", "MANAGER", "ADMIN")
                .requestMatchers("/tickets/view/**").authenticated()
                
                // User-specific endpoints
                .requestMatchers("/tickets/create").hasAnyAuthority("USER")
                .requestMatchers("/tickets/*/close", "/tickets/*/reopen").hasAnyAuthority("USER")
                
                // Manager-specific endpoints
                .requestMatchers("/tickets/pending_approval").hasAnyAuthority("MANAGER")
                .requestMatchers("/tickets/*/approve", "/tickets/*/reject").hasAnyAuthority("MANAGER")
                .requestMatchers("/tickets/approved", "/tickets/*/assign").hasAnyAuthority("MANAGER", "ADMIN")
                
                // Admin-specific endpoints
                .requestMatchers("/tickets/assigned").hasAuthority("ADMIN")
                .requestMatchers("/tickets/*/resolve").hasAuthority("ADMIN")
                
                // API endpoints
                .requestMatchers("/api/tickets/**").authenticated()
                
                // Dashboards
                .requestMatchers("/dashboard").authenticated()
                .requestMatchers("/dashboard/user/**").hasAuthority("USER")
                .requestMatchers("/dashboard/manager/**").hasAuthority("MANAGER")
                .requestMatchers("/dashboard/admin/**").hasAuthority("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIOINID")
                .permitAll()
            );
        
        http.csrf(csrf -> csrf.disable());
        http.headers(headers -> headers.frameOptions(frame -> frame.disable()));
        
        return http.build();
    }
}
