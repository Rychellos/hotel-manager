package pl.rychellos.hotel.authorization.permission;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.permission.dto.PermissionFilterDTO;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.role.RoleRepository;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Service
public class PermissionService
        extends GenericService<PermissionEntity, PermissionDTO, PermissionFilterDTO, PermissionRepository> {
    private final RoleRepository roleRepository;

    public PermissionService(
            LangUtil languageUtil,
            PermissionMapper mapper,
            PermissionRepository repository,
            ApplicationExceptionFactory exceptionFactory,
            ObjectMapper objectMapper, RoleRepository roleRepository) {
        super(languageUtil, PermissionDTO.class, mapper, repository, exceptionFactory, objectMapper);
        this.roleRepository = roleRepository;
    }

    @Override
    protected void fetchRelations(PermissionEntity entity, PermissionDTO dto) throws ApplicationException {
        if (dto.getRoleIds() == null) {
            return;
        }

        if (dto.getRoleIds().isEmpty()) {
            entity.getRoles().clear();
            return;
        }

        List<RoleEntity> roles = roleRepository.findAllById(dto.getRoleIds());
        entity.setRoles(new HashSet<>(roles));
    }
}
