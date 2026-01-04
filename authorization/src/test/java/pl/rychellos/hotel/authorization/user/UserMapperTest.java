package pl.rychellos.hotel.authorization.user;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.user.dto.UserDTO;

import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper mapper = Mappers.getMapper(UserMapper.class);

    @Test
    void toDTO() {
        /// Given
        RoleEntity roleEntity1 = new RoleEntity();
        roleEntity1.setId(1L);
        roleEntity1.setName("ROLE_1");
        RoleEntity roleEntity2 = new RoleEntity();
        roleEntity2.setName("ROLE_2");
        roleEntity2.setId(2L);

        UserEntity entity = new UserEntity();
        entity.setId(10L);
        entity.setUsername("USER_1");
        entity.setEmail("USER_1@localhost");
        entity.setPassword("password");
        entity.setRoles(Set.of(roleEntity1, roleEntity2));

        /// When
        UserDTO dto = mapper.toDTO(entity);

        /// Then
        assertNotNull(dto);
        assertEquals(10L, dto.getId());
        assertEquals("USER_1", dto.getUsername());
        assertEquals("USER_1@localhost", dto.getEmail());

        assertEquals(Set.of(1L, 2L), dto.getRoleIds());
    }

    @Test
    void toEntity() {
        /// Given
        UserDTO dto = new UserDTO();
        dto.setId(20L);
        dto.setUsername("USER_2");
        dto.setEmail("USER_2@localhost");
        dto.setRoleIds(Set.of(3L, 4L));

        /// When
        UserEntity entity = mapper.toEntity(dto);

        /// Then
        assertNotNull(entity);
        assertEquals(20L, entity.getId());
        assertEquals("USER_2", entity.getUsername());
        assertEquals("USER_2@localhost", entity.getEmail());

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
        existingRole.setName("ROLE_1");

        UserEntity entity = new UserEntity();
        entity.setId(30L);
        entity.setUsername("USER_3_OLD");
        entity.setRoles(new HashSet<>(Set.of(existingRole)));

        UserDTO dto = new UserDTO();
        dto.setUsername("USER_3_NEW");
        dto.setRoleIds(Set.of(5L));

        /// When
        mapper.updateEntityFromDTO(entity, dto);

        /// Then
        assertEquals("USER_3_NEW", entity.getUsername());

        assertEquals(1, entity.getRoles().size());
        assertTrue(entity.getRoles().contains(existingRole));
    }
}
