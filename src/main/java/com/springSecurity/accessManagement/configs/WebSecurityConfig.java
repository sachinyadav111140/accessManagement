package com.springSecurity.accessManagement.configs;

import com.springSecurity.accessManagement.helper.JwtTokenUtil;
import com.springSecurity.accessManagement.services.UserServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    @Autowired
    private  UserServiceImpl userServiceImpl;
    @Autowired
    private  JwtTokenUtil jwtTokenUtil;
    @Autowired
    private  AuthEntryPoint unauthorizedHandler;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userServiceImpl);
        authProvider.setPasswordEncoder(bCryptPasswordEncoder);
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable);
        http.authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/public/**"),
                                new AntPathRequestMatcher("/swagger-ui/index.html"),
                                new AntPathRequestMatcher("/swagger-ui/**"),
                                new AntPathRequestMatcher("/v3/api-docs/**"),
                                new AntPathRequestMatcher("/swagger-resources/**"),
                                new AntPathRequestMatcher("/webjars/**"),
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/uploads/**"),
                                new AntPathRequestMatcher("/favicon.ico")
                        ).permitAll()
                        .anyRequest().authenticated()
                );
        http.exceptionHandling().authenticationEntryPoint(unauthorizedHandler);
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.addFilterBefore(authenticationTokenFilterBean(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationFilter authenticationTokenFilterBean() {
        return new AuthenticationFilter(userServiceImpl, jwtTokenUtil);
    }
}
