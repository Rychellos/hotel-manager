package pl.rychellos.hotel.authorization.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.permission.PermissionMapper;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.role.RoleRepository;
import pl.rychellos.hotel.authorization.role.RoleService;
import pl.rychellos.hotel.authorization.user.dto.UserDTO;
import pl.rychellos.hotel.authorization.user.dto.UserFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Slf4j
@Service
public class UserService extends GenericService<UserEntity, UserDTO, UserFilterDTO, UserRepository>
        implements UserDetailsPasswordService, UserDetailsService {
    private final PermissionMapper permissionMapper;
    private final RoleService roleService;
    private final RoleRepository roleRepository;

    public UserService(
            UserRepository repository,
            UserMapper mapper,
            ApplicationExceptionFactory exceptionFactory,
            LangUtil langUtil,
            ObjectMapper objectMapper, PermissionMapper permissionMapper, RoleService roleService,
            RoleRepository roleRepository) {
        super(langUtil, UserDTO.class, mapper, repository, exceptionFactory, objectMapper);
        this.permissionMapper = permissionMapper;
        this.roleService = roleService;
        this.roleRepository = roleRepository;
    }

    public List<PermissionDTO> getPermissions(Long id) throws ApplicationException {
        return repository.findById(id).orElseThrow(() -> applicationExceptionFactory.resourceNotFound(
                langUtil.getMessage("error.user.notFound.byId", id.toString())))
                .getRoles().stream().flatMap(role -> role.getPermissions().stream())
                .map(permissionMapper::toDTO)
                .collect(Collectors.toList());
    }

    private UserEntity updatePasswordInternal(String username, String newPassword) throws ApplicationException {
        Optional<UserEntity> optionalUserEntity = this.repository.findByUsername(username);

        if (optionalUserEntity.isEmpty()) {
            log.info("Tried to change password of user with username {}, but user with this username doesn't exist",
                    username);

            throw applicationExceptionFactory.resourceNotFound(
                    langUtil.getMessage("error.user.notFound.byUsername", username));
        }

        log.info("Changed {}'s password", username);

        UserEntity userEntity = optionalUserEntity.get();

        userEntity.setPassword(newPassword);

        return this.repository.save(userEntity);
    }

    public UserDTO updatePassword(String username, String newPassword) throws ApplicationException {
        return mapper.toDTO(this.updatePasswordInternal(username, newPassword));
    }

    public UserDTO updatePassword(UserDTO userDTO, String newPassword) throws ApplicationException {
        return mapper.toDTO(this.updatePasswordInternal(userDTO.getUsername(), newPassword));
    }

    @Override
    public UserDetails updatePassword(@NonNull UserDetails user, @Nullable String newPassword) {
        try {
            return this.updatePasswordInternal(user.getUsername(), newPassword);
        } catch (ApplicationException e) {
            throw new RuntimeException(e);
        }
    }

    private UserEntity fetchByUsername(String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException(
                    langUtil.getMessage("error.user.username.empty"));
        }

        Optional<UserEntity> optionalUserEntity = this.repository.findByUsername(username);

        if (optionalUserEntity.isEmpty()) {
            log.error("Tried to fetch user with username {}, but user with this username doesn't exist", username);

            throw new UsernameNotFoundException(
                    langUtil.getMessage("error.user.notFound.byUsername", username));
        }

        return optionalUserEntity.get();
    }

    public UserDTO getByUsername(String username) throws ApplicationException {
        try {
            return mapper.toDTO(fetchByUsername(username));
        } catch (UsernameNotFoundException exception) {
            throw applicationExceptionFactory.resourceNotFound(exception.getMessage());
        }
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        UserEntity user = fetchByUsername(username);

        log.info("Loaded user with username {}", username);

        return user;
    }

    @Override
    protected void fetchRelations(UserEntity entity, UserDTO dto) throws ApplicationException {
        if (dto.getRoleIds() != null) {
            if (dto.getRoleIds().isEmpty()) {
                entity.getRoles().clear();
            } else {
                List<RoleEntity> roles = roleRepository.findAllById(dto.getRoleIds());
                entity.setRoles(new HashSet<>(roles));
            }
        }
    }
}
