package pl.rychellos.hotel.authorization.configuration;

import java.io.Serializable;
import java.util.Objects;
import org.jspecify.annotations.NonNull;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;
import pl.rychellos.hotel.lib.security.PermissionDef;

@Component
public class CustomPermissionEvaluator implements PermissionEvaluator {
    @Override
    public boolean hasPermission(
        @NonNull Authentication authentication,
        @NonNull Object targetDomainObject,
        @NonNull Object permission
    ) {
        return hasPrivilege(authentication, targetDomainObject.toString(), permission.toString());
    }

    @Override
    public boolean hasPermission(
        @NonNull Authentication authentication,
        @NonNull Serializable targetId,
        @NonNull String targetType,
        @NonNull Object permission
    ) {
        return hasPrivilege(
            authentication,
            targetId.toString(),
            targetType + ":" + permission
        );
    }

    private boolean hasPrivilege(Authentication authentication, String target, String actionAndScope) {
        String requiredPermission = target + ":" + actionAndScope;
        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(Objects::nonNull)
            .anyMatch(grantedAuthority -> grantedAuthority.equals(requiredPermission)
                || grantedAuthority.equals("ROLE_ADMIN"));
    }

    public boolean hasPermission(Authentication authentication, String target, ActionType action, ActionScope scope) {
        String requiredPermission = PermissionDef.buildInternalString(target, action.toString(), scope.toString());

        return authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .filter(Objects::nonNull)
            .anyMatch(grantedAuthority -> grantedAuthority.equals(requiredPermission) || grantedAuthority.equals("ROLE_ADMIN"));
    }
}
