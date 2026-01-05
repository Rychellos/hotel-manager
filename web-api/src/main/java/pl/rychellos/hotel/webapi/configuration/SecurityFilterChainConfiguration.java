package pl.rychellos.hotel.webapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;
import pl.rychellos.hotel.authorization.JwtAuthenticationFilter;
import pl.rychellos.hotel.authorization.service.JwtService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Configuration
class SecurityFilterChainConfiguration {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    SecurityFilterChainConfiguration(
        JwtService jwtService,
        UserDetailsService userDetailsService
    ) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, HandlerExceptionResolver handlerExceptionResolver, ApplicationExceptionFactory applicationExceptionFactory, LangUtil langUtil) throws Exception {
        http
            .securityMatcher("/api/v1/**")
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(req -> req
                .requestMatchers(
                    "/api/v1/auth/**"
                )
                .permitAll()
                .anyRequest()
                .authenticated()
            )
            .addFilterBefore(new JwtAuthenticationFilter(jwtService, userDetailsService, handlerExceptionResolver, applicationExceptionFactory, langUtil), UsernamePasswordAuthenticationFilter.class)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}
