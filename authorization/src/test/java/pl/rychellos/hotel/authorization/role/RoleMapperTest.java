package pl.rychellos.hotel.authorization.role;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.rychellos.hotel.authorization.permission.PermissionEntity;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.authorization.user.UserEntity;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleMapperTest {
    private final RoleMapper mapper = Mappers.getMapper(RoleMapper.class);

    @Test
    void toDTO() {
        /// Given
        PermissionEntity perm1 = new PermissionEntity();
        perm1.setId(1L);
        PermissionEntity perm2 = new PermissionEntity();
        perm2.setId(2L);

        UserEntity userEntity = new UserEntity();
        userEntity.setId(10L);
        userEntity.setUsername("USER_1");

        RoleEntity entity = new RoleEntity();
        entity.setId(10L);
        entity.setName("ROLE_ADMIN");
        entity.setPermissions(Set.of(perm1, perm2));
        entity.setUsers(Set.of(userEntity));

        /// When
        RoleDTO dto = mapper.toDTO(entity);

        /// Then
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("ROLE_ADMIN", dto.getName());

        assertEquals(Set.of(1L, 2L), dto.getPermissionIds());
        assertEquals(Set.of(10L), dto.getUserIds());
    }

    @Test
    void toEntity() {
        /// Given
        RoleDTO dto = new RoleDTO();
        dto.setId(99L);
        dto.setName("ROLE_ADMIN");
        dto.setPermissionIds(Set.of(1L, 2L));
        dto.setUserIds(Set.of(10L, 20L));

        /// WHEN
        RoleEntity entity = mapper.toEntity(dto);

        /// THEN
        assertNotNull(entity);
        assertEquals(99L, entity.getId());
        assertEquals("ROLE_ADMIN", entity.getName());

        assertTrue(
            entity.getPermissions() == null || entity.getPermissions().isEmpty(),
            "Mapper should ignore relationships that require DB lookup"
        );
        assertTrue(
            entity.getUsers() == null || entity.getUsers().isEmpty(),
            "Mapper should ignore relationships that require DB lookup"
        );
    }

    @Test
    void updateEntityFromDTO() {
        /// Given
        PermissionEntity existingPermission = new PermissionEntity();
        existingPermission.setId(1L);

        UserEntity existingUser = new UserEntity();
        existingUser.setId(10L);

        RoleEntity entity = new RoleEntity();
        entity.setId(99L);
        entity.setName("ROLE_ADMIN_OLD");
        entity.setPermissions(Set.of(existingPermission));
        entity.setUsers(Set.of(existingUser));

        RoleDTO dto = new RoleDTO();
        dto.setId(99L);
        dto.setName("ROLE_ADMIN_NEW");

        /// WHEN
        mapper.updateEntityFromDTO(entity, dto);

        /// THEN
        assertEquals("ROLE_ADMIN_NEW", dto.getName());

        assertEquals(1, entity.getPermissions().size());
        assertTrue(entity.getPermissions().contains(existingPermission));

        assertEquals(1, entity.getUsers().size());
        assertTrue(entity.getUsers().contains(existingUser));
    }
}
