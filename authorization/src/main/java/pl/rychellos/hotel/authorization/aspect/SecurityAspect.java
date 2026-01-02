package pl.rychellos.hotel.authorization.aspect;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.authorization.configuration.CustomPermissionEvaluator;

@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAspect {

    private final CustomPermissionEvaluator permissionEvaluator;

    @Before("@annotation(checkPermission)")
    public void checkPermission(CheckPermission checkPermission) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new AccessDeniedException("No authentication found");
        }

        boolean hasPermission = permissionEvaluator.hasPermission(
                authentication,
                checkPermission.target(),
                checkPermission.action(),
                checkPermission.scope());

        if (!hasPermission) {
            throw new AccessDeniedException("Access denied");
        }
    }
}
