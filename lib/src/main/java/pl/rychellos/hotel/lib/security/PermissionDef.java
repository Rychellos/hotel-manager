package pl.rychellos.hotel.lib.security;

public record PermissionDef(String target, ActionType action, ActionScope scope) {
    public String toPermissionString() {
        return String.format("%s:%s:%s", target, action, scope);
    }

    public static String buildInternalString(String target, String action, String scope) {
        return String.format("%s:%s:%s", target, action, scope);
    }
}
