package pl.rychellos.hotel.authorization.permission;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.role.RoleEntity;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class PermissionMapperTest {
    private final PermissionMapper mapper = Mappers.getMapper(PermissionMapper.class);

    @Test
    void toDTO() {
        /// Given
        RoleEntity role1 = new RoleEntity();
        role1.setId(1L);
        RoleEntity role2 = new RoleEntity();
        role2.setId(2L);

        PermissionEntity entity = new PermissionEntity();
        entity.setId(10L);
        entity.setName("READ_PRIVILEGE");
        entity.setRoles(Set.of(role1, role2));

        /// When
        PermissionDTO dto = mapper.toDTO(entity);

        /// Then
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("READ_PRIVILEGE", dto.getName());

        assertEquals(Set.of(1L, 2L), dto.getRoleIds());
    }

    @Test
    void toEntity() {
        /// Given
        PermissionDTO dto = new PermissionDTO();
        dto.setId(20L);
        dto.setName("WRITE_PRIVILEGE");
        dto.setRoleIds(Set.of(3L, 4L));

        /// When
        PermissionEntity entity = mapper.toEntity(dto);

        /// Then
        assertNotNull(entity);
        assertEquals(20L, entity.getId());
        assertEquals("WRITE_PRIVILEGE", entity.getName());

        assertTrue(
            entity.getRoles() == null || entity.getRoles().isEmpty(),
            "Mapper should ignore relationships that require DB lookup"
        );
    }

    @Test
    void updateEntityFromDTO() {
        /// Given
        RoleEntity existingRole = new RoleEntity();
        existingRole.setId(99L);

        PermissionEntity entity = new PermissionEntity();
        entity.setId(30L);
        entity.setName("OLD_PERMISSION");
        entity.setRoles(new HashSet<>(Set.of(existingRole)));

        PermissionDTO dto = new PermissionDTO();
        dto.setName("NEW_PERMISSION");
        dto.setRoleIds(Set.of(5L));

        /// When
        mapper.updateEntityFromDTO(entity, dto);

        /// Then
        assertEquals("NEW_PERMISSION", entity.getName());

        assertEquals(1, entity.getRoles().size());
        assertTrue(entity.getRoles().contains(existingRole));
    }
}