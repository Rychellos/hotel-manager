package pl.rychellos.hotel.webapi.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig {

    @Controller
    public static class SpaController {
        @RequestMapping(value = { "/{path:[^\\.]*}", "/" })
        public String redirect() {
            return "forward:/index.html";
        }
    }
}
