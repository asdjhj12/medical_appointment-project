package com.medical.config;

import com.medical.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;


@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .antMatchers(
                                "/api/users/login",
                                "/api/users/register",
                                "/api/users/info",
                                "/api/admin-users/**",
                                "/api/appointments/today/count",
                                "/api/appointments/doctor/**",
                                "/api/appointments/user/**",
                                "/api/appointments",
                                "/api/doctors/active/count",
                                "/api/appointments/waiting/count",
                                "/api/patient/list",
                                "/api/patient/user/**",
                                "/api/medicine-stock-records",
                                "/api/medical-records/doctor/**",
                                "/api/medical-records/patient/**",
                                "/api/medical-records/detail/**",
                                "/api/medical-records/take-medicine",
                                "/api/medical-records",
                                "/api/departments/**",
                                "/api/departments",
                                "/api/doctor-schedules/**",
                                "/login",
                                "/index.html",
                                "/",
                                "/css/**",
                                "/js/**",
                                "/img/**",
                                "/fonts/**",
                                "/favicon.ico",
                                "/api/doctors/**",
                                "/error",
                                "/api/patient/appointments/user/**",
                                "/api/appointments/user/*/page"
                        ).permitAll()
                        .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .anyRequest().authenticated()
                )
                .anonymous().authorities("ROLE_ANONYMOUS").and()
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/api/users/login",
                "/api/users/register",
                "/api/admin-users/**",
                "/api/appointments/today/count", "/api/doctors/active/count",
                "/api/appointments/waiting/count",
                "/api/appointments/user/**",
                "/api/appointments",
                "/api/patient/list",
                "/api/medicine-stock-records",
                "/api/medical-records/patient/**",
                "/api/medical-records/detail/**",
                "/api/medical-records/take-medicine",
                "/api/medical-records",
                "/api/departments/**",
                "/api/appointments/doctor/**",
                "/api/users/info",
                "/api/patient/user/**",
                "/api/departments",
                "/api/doctor-schedules/**",
                "/api/patient/appointments/user/**",
                "/error" ,
                "/login", "/index.html", "/", "/css/**", "/js/**", "/img/**", "/fonts/**", "/favicon.ico"
        );
    }


}