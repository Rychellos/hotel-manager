package pl.rychellos.hotel.webapi.configuration;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig {
    @Controller
    public static class SpaFallbackController {
        @GetMapping(value = {
            "/",
            "/{path:^(?!api|_build|_server|favicon\\.ico|icon\\.png|index\\.html).*}",
            "/{path:^(?!api|_build|_server|favicon\\.ico|icon\\.png|index\\.html).*}/**"
        })
        public String forward() {
            return "forward:/index.html";
        }
    }
}
