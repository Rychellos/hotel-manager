package pl.rychellos.hotel.webapi.configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.responses.ApiResponses;
import io.swagger.v3.oas.models.security.OAuthFlow;
import io.swagger.v3.oas.models.security.OAuthFlows;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.RequestMapping;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Configuration
public class SwaggerConfiguration {
    private SecurityScheme oauth2PasswordScheme() {
        return new SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .flows(new OAuthFlows()
                .password(new OAuthFlow()
                    // Replace with your actual login endpoint URL
                    .tokenUrl("/api/v1/auth/login")
                    .refreshUrl("/api/v1/auth/refresh")
                )
            );
    }

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
            .info(new Info()
                .title("Hotel Management API")
                .version("1.0")
                .description("API for managing rooms in hotels.")
            )
            .addSecurityItem(new SecurityRequirement()
                .addList("spring_oauth")
            )
            .components(new Components()
                .addSecuritySchemes("spring_oauth", oauth2PasswordScheme())
                .addSchemas("ProblemDetail", new Schema<ProblemDetail>()
                    .type("object")
                    .addProperty("type", new StringSchema().example("about:blank"))
                    .addProperty("title", new StringSchema().example("Bad Request"))
                    .addProperty("status", new IntegerSchema().example(400))
                    .addProperty("detail", new StringSchema().example("Invalid input parameters"))
                    .addProperty("instance", new StringSchema().example("/api/orders/123"))
                )
            );
    }

    @Bean
    public OperationCustomizer factoryDrivenCustomizer(ApplicationExceptionFactory factory) {
        final HttpStatus[] DEFAULT_STATUSES = {
            HttpStatus.BAD_REQUEST,
            HttpStatus.UNAUTHORIZED,
            HttpStatus.INTERNAL_SERVER_ERROR
        };

        return (operation, handlerMethod) -> {
            ApiProblemResponses annotation = handlerMethod.getMethodAnnotation(ApiProblemResponses.class);
            if (annotation == null) {
                annotation = handlerMethod.getBeanType().getAnnotation(ApiProblemResponses.class);
            }

            // 2. Resolve statuses: Use annotation values OR fall back to defaults
            HttpStatus[] statusesToDocument = (annotation != null) ? annotation.value() : DEFAULT_STATUSES;

            // 1. Resolve the Full Path for 'instance'
            RequestMapping classMapping = handlerMethod.getBeanType().getAnnotation(RequestMapping.class);
            String basePath = (classMapping != null && classMapping.value().length > 0) ? classMapping.value()[0] : "";
            RequestMapping methodMapping = handlerMethod.getMethodAnnotation(RequestMapping.class);
            String methodPath = (methodMapping != null && methodMapping.value().length > 0) ? methodMapping.value()[0] : "";
            String fullPath = (basePath + methodPath).replace("//", "/");

            ApiResponses responses = operation.getResponses();

            // 2. Define our targets by mapping Status -> Factory Method
            // We "dry run" the factory to get the real Title strings
            for (HttpStatus status : statusesToDocument) {
                final String detailPlaceholder = "Error details";

                ApplicationException ex = switch (status) {
                    case BAD_REQUEST -> factory.badRequest(detailPlaceholder);
                    case UNAUTHORIZED -> factory.unauthorized(detailPlaceholder);
                    case FORBIDDEN -> factory.forbidden(detailPlaceholder);
                    case NOT_FOUND -> factory.resourceNotFound(detailPlaceholder);
                    case METHOD_NOT_ALLOWED -> factory.methodNotAllowed(detailPlaceholder);
                    case CONFLICT -> factory.conflict(detailPlaceholder);
                    case INTERNAL_SERVER_ERROR -> factory.internalServerError(detailPlaceholder);
                    default -> throw new IllegalStateException("Unexpected value: " + status);
                };

                addResponse(responses, ex, fullPath);
            }

            return operation;
        };
    }

    private void addResponse(ApiResponses responses, ApplicationException ex, String path) {
        String code = String.valueOf(ex.getStatus().value());

        // Create the schema override
        Schema<?> problemSchema = new Schema<>().$ref("#/components/schemas/ProblemDetail");
        problemSchema.addProperty("status", new IntegerSchema().example(ex.getStatus().value()));
        problemSchema.addProperty("title", new StringSchema().example(ex.getTitle())); // From your LangUtil/Factory
        problemSchema.addProperty("instance", new StringSchema().example(path));
        problemSchema.addProperty("detail", new StringSchema().example(ex.getDetail()));

        Content content = new Content().addMediaType("application/problem+json",
            new MediaType().schema(problemSchema));

        responses.addApiResponse(code, new ApiResponse()
            .description(ex.getTitle())
            .content(content));
    }

    @Target({ElementType.METHOD, ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface ApiProblemResponses {
        HttpStatus[] value() default {
            HttpStatus.BAD_REQUEST,
            HttpStatus.INTERNAL_SERVER_ERROR
        };
    }
}
