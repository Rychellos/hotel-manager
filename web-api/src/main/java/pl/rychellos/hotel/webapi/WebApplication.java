package pl.rychellos.hotel.webapi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "pl.rychellos.hotel")
@EnableJpaRepositories(basePackages = "pl.rychellos.hotel")
@EntityScan(basePackages = "pl.rychellos.hotel")
class WebApplication {

    public static void main(String[] args) {
        SpringApplication.run(WebApplication.class, args);
    }
}
