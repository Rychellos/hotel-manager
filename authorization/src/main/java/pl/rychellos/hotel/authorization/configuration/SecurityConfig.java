package pl.rychellos.hotel.authorization.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.authorization.user.UserService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    private final PasswordEncoder passwordEncoder;
    private final UserService userService;

    public SecurityConfig(UserRepository userService, PasswordEncoder passwordEncoder, UserService userRepository1) {
        this.passwordEncoder = passwordEncoder;
        this.userService = userRepository1;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationProvider provider) throws Exception {
        return new ProviderManager(provider);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userService);
        authProvider.setUserDetailsPasswordService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }
}
