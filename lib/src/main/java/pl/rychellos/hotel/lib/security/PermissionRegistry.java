package pl.rychellos.hotel.lib.security;

import java.util.Set;

public interface PermissionRegistry {
    Set<PermissionDef> getPermissions();
}
