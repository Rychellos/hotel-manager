package pl.rychellos.hotel.webapi.configuration;

import static org.springframework.data.web.config.EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = VIA_DTO)
public class WebConfig {

    @RestControllerAdvice
    public static class SpaController {

        @ExceptionHandler(HttpClientErrorException.NotFound.class)
        public Object redirect(HttpServletRequest request, HttpServletResponse response, HttpClientErrorException.NotFound error) {
            if (request.getMethod().equalsIgnoreCase(HttpMethod.GET.name())) {
                response.setStatus(HttpStatus.OK.value());
                return "forward:/index.html";
            } else {
                return response;
            }
        }
    }
}
