package pl.rychellos.hotel.authorization.configuration;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@AutoConfiguration
@ComponentScan("pl.rychellos.hotel.authorization")
@EntityScan("pl.rychellos.hotel.authorization")
@EnableJpaRepositories("pl.rychellos.hotel.authorization")
public class AuthAutoConfig {
}
