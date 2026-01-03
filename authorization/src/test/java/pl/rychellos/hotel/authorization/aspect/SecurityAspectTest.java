package pl.rychellos.hotel.authorization.aspect;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.authorization.configuration.CustomPermissionEvaluator;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = SecurityAspectTest.Config.class)
public class SecurityAspectTest {

    @Autowired
    private AspectTestService testService;

    @Autowired
    private CustomPermissionEvaluator permissionEvaluator;

    @Test
    public void testCheckPermissionAllowed() {
        // Setup Security Context
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken("user", "pass", Collections.emptyList()));

        // Mock evaluator to return true
        when(permissionEvaluator.hasPermission(any(Authentication.class), eq("ROOM"), eq(ActionType.READ),
            eq(ActionScope.PAGINATED)))
            .thenReturn(true);

        assertDoesNotThrow(() -> testService.protectedMethod());
    }

    @Test
    public void testCheckPermissionDenied() {
        // Setup Security Context
        SecurityContextHolder.getContext()
            .setAuthentication(new UsernamePasswordAuthenticationToken("user", "pass", Collections.emptyList()));

        // Mock evaluator to return false
        when(permissionEvaluator.hasPermission(any(Authentication.class), eq("ROOM"), eq(ActionType.READ),
            eq(ActionScope.PAGINATED)))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> testService.protectedMethod());
    }

    @Configuration
    @EnableAspectJAutoProxy
    @Import({SecurityAspect.class})
    static class Config {
        @Bean
        public CustomPermissionEvaluator customPermissionEvaluator() {
            return mock(CustomPermissionEvaluator.class);
        }

        @Bean
        public AspectTestService aspectTestService() {
            return new AspectTestService();
        }
    }

    @Component
    public static class AspectTestService {
        @CheckPermission(target = "ROOM", action = ActionType.READ, scope = ActionScope.PAGINATED)
        public void protectedMethod() {
            // Do nothing
        }
    }
}
