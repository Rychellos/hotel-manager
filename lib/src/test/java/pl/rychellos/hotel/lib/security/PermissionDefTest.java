package pl.rychellos.hotel.lib.security;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PermissionDefTest {

    @Test
    void toPermissionString_ShouldFormatCorrectly() {
        /// Given
        PermissionDef permission = new PermissionDef("USER", ActionType.READ, ActionScope.ALL);

        /// When
        String result = permission.toPermissionString();

        /// Then
        // Verifies the format "target:action:scope"
        assertEquals("USER:READ:ALL", result);
    }

    @Test
    void buildInternalString_ShouldMatchFormat() {
        /// When
        String result = PermissionDef.buildInternalString("BOOKING", "WRITE", "ONE");

        /// Then
        assertEquals("BOOKING:WRITE:ONE", result);
    }
}