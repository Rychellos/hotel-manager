package pl.rychellos.hotel.authorization.role;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import pl.rychellos.hotel.authorization.permission.PermissionEntity;
import pl.rychellos.hotel.authorization.permission.PermissionRepository;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.lib.testing.BaseServiceTest;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RoleServiceTest extends BaseServiceTest<RoleEntity, RoleDTO, RoleService, RoleRepository> {

    @Mock
    private RoleMapper roleMapper;

    @Mock
    private PermissionRepository permissionRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        /// We link our specific mock to the generic parent field
        this.mapper = roleMapper;

        this.repository = roleRepository;

        /// Initialize service with generic mocks from the parent and specific mocks from here
        this.service = new RoleService(
            repository,
            roleMapper,
            exceptionFactory,
            langUtil,
            objectMapper,
            permissionRepository,
            userRepository
        );
    }

    @Override
    protected RoleEntity createEntity(Long id) {
        RoleEntity role = new RoleEntity();
        role.setId(id);
        role.setName("ROLE_ADMIN");
        role.setDescription("Administrator role");
        role.setPermissions(new HashSet<>());
        role.setUsers(new HashSet<>());
        return role;
    }

    @Override
    protected RoleDTO createDTO(Long id) {
        RoleDTO dto = new RoleDTO();
        dto.setId(id);
        dto.setName("ROLE_ADMIN");
        dto.setDescription("Administrator role");
        return dto;
    }

    @Test
    void fetchRelations_ShouldSetPermissionsAndUsers_WhenIdsAreProvided() {
        /// Given
        RoleEntity entity = createEntity(1L);
        RoleDTO dto = createDTO(1L);
        dto.setPermissionIds(Set.of(10L, 11L));
        dto.setUserIds(Set.of(100L));

        PermissionEntity perm1 = new PermissionEntity();
        perm1.setId(10L);
        PermissionEntity perm2 = new PermissionEntity();
        perm2.setId(11L);
        UserEntity user = new UserEntity();
        user.setId(100L);

        when(permissionRepository.findAllById(dto.getPermissionIds())).thenReturn(List.of(perm1, perm2));
        when(userRepository.findAllById(dto.getUserIds())).thenReturn(List.of(user));

        /// When
        service.fetchRelations(entity, dto);

        /// Then
        assertEquals(2, entity.getPermissions().size());
        assertEquals(1, entity.getUsers().size());
        assertTrue(entity.getPermissions().contains(perm1));
        assertTrue(entity.getUsers().contains(user));

        verify(permissionRepository).findAllById(any());
        verify(userRepository).findAllById(any());
    }

    @Test
    void fetchRelations_ShouldClearCollections_WhenIdsAreEmpty() {
        /// Given
        RoleEntity entity = createEntity(1L);
        entity.getPermissions().add(new PermissionEntity());
        entity.getUsers().add(new UserEntity());

        RoleDTO dto = createDTO(1L);
        dto.setPermissionIds(Set.of());
        dto.setUserIds(Set.of());

        /// When
        service.fetchRelations(entity, dto);

        /// Then
        assertTrue(entity.getPermissions().isEmpty());
        assertTrue(entity.getUsers().isEmpty());

        verifyNoInteractions(permissionRepository);
        verifyNoInteractions(userRepository);
    }

    @Test
    void fetchRelations_ShouldIgnoreCollections_WhenIdsAreNull() {
        /// Given
        RoleEntity entity = createEntity(1L);
        PermissionEntity existingPerm = new PermissionEntity();
        entity.getPermissions().add(existingPerm);

        RoleDTO dto = createDTO(1L);
        dto.setPermissionIds(null); // Should not trigger clear or fetch
        dto.setUserIds(null);

        /// When
        service.fetchRelations(entity, dto);

        /// Then
        assertEquals(1, entity.getPermissions().size());
        assertTrue(entity.getPermissions().contains(existingPerm));

        verifyNoInteractions(permissionRepository);
        verifyNoInteractions(userRepository);
    }
}