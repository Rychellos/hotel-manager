package pl.rychellos.hotel.authorization.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.permission.PermissionEntity;
import pl.rychellos.hotel.authorization.permission.PermissionMapper;
import pl.rychellos.hotel.authorization.permission.PermissionRepository;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.authorization.role.dto.RoleFilterDTO;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Service
public class RoleService extends GenericService<RoleEntity, RoleDTO, RoleFilterDTO, RoleRepository> {
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;
    private final PermissionMapper permissionMapper;

    public RoleService(
            RoleRepository repository,
            RoleMapper mapper,
            ApplicationExceptionFactory exceptionFactory,
            LangUtil languageUtil,
            ObjectMapper objectMapper, PermissionRepository permissionRepository, UserRepository userRepository,
            PermissionMapper permissionMapper) {
        super(languageUtil, RoleDTO.class, mapper, repository, exceptionFactory, objectMapper);
        this.permissionRepository = permissionRepository;
        this.userRepository = userRepository;
        this.permissionMapper = permissionMapper;
    }

    public List<PermissionDTO> getPermissions(Long id) throws ApplicationException {
        return repository.findById(id).orElseThrow(() -> applicationExceptionFactory.resourceNotFound(
                langUtil.getMessage("error.role.notFound.byId", id.toString())))
                .getPermissions().stream()
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    protected void fetchRelations(RoleEntity entity, RoleDTO dto) throws ApplicationException {
        if (dto.getPermissionIds() != null) {
            if (dto.getPermissionIds().isEmpty()) {
                entity.getPermissions().clear();
            } else {
                List<PermissionEntity> permissions = permissionRepository.findAllById(dto.getPermissionIds());
                entity.setPermissions(new HashSet<>(permissions));
            }
        }

        if (dto.getUserIds() != null) {
            if (dto.getUserIds().isEmpty()) {
                entity.getUsers().clear();
            } else {
                List<UserEntity> users = userRepository.findAllById(dto.getUserIds());
                entity.setUsers(new HashSet<>(users));
            }
        }
    }
}
