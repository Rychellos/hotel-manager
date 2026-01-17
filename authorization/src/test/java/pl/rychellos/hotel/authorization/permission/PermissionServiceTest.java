package pl.rychellos.hotel.authorization.permission;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.role.RoleRepository;
import pl.rychellos.hotel.lib.testing.BaseServiceTest;

class PermissionServiceTest
        extends BaseServiceTest<PermissionEntity, PermissionDTO, PermissionService, PermissionRepository> {
    @Mock
    private PermissionMapper permissionMapper;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PermissionRepository permissionRepository;

    @BeforeEach
    void setUp() {
        this.mapper = permissionMapper;

        this.repository = permissionRepository;

        this.service = new PermissionService(
                langUtil, permissionMapper, repository,
                exceptionFactory, objectMapper, roleRepository);
    }

    @Override
    protected PermissionEntity createEntity(Long id) {
        PermissionEntity entity = new PermissionEntity();
        entity.setId(id);
        entity.setName("READ_PERM");
        return entity;
    }

    @Override
    protected PermissionDTO createDTO(Long id) {
        PermissionDTO dto = new PermissionDTO();
        dto.setId(id);
        dto.setName("READ_PERM");
        return dto;
    }

    /// Specific test for the logic unique to PermissionService
    @Test
    void fetchRelations_ShouldSetRoles() throws Exception {
        /// Given
        PermissionEntity entity = new PermissionEntity();
        PermissionDTO dto = createDTO(1L);
        dto.setRoleIds(Set.of(10L));

        RoleEntity role = new RoleEntity();
        role.setId(10L);
        when(roleRepository.findAllById(any())).thenReturn(List.of(role));

        /// When
        // We can call the protected method directly because we are in the same package
        service.fetchRelations(entity, dto);

        /// Then
        assertEquals(1, entity.getRoles().size());
        assertTrue(entity.getRoles().contains(role));
    }
}
