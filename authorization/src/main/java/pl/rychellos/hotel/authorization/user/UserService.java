package pl.rychellos.hotel.authorization.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.user.dto.UserDTO;
import pl.rychellos.hotel.authorization.user.dto.UserFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Optional;

@Slf4j
@Service
public class UserService extends GenericService<
    UserEntity,
    UserDTO,
    UserFilterDTO,
    UserRepository
    > implements UserDetailsPasswordService, UserDetailsService {
    public UserService(
        UserRepository repository,
        UserMapper mapper,
        ApplicationExceptionFactory exceptionFactory,
        LangUtil langUtil,
        ObjectMapper objectMapper) {
        super(langUtil, UserDTO.class, mapper, repository, exceptionFactory, objectMapper);
    }

    private UserEntity updatePasswordInternal(String username, String newPassword) throws ApplicationException {
        Optional<UserEntity> optionalUserEntity = this.repository.findByUsername(username);

        if (optionalUserEntity.isEmpty()) {
            log.info("Tried to change password of user with username {}, but user with this username doesn't exist", username);

            throw applicationExceptionFactory.resourceNotFound(
                langUtil.getMessage("error.user.notFound.byUsername", username)
            );
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
    public UserDetails updatePassword(@NonNull UserDetails user, @Nullable String newPassword) throws ApplicationException {
        return this.updatePasswordInternal(user.getUsername(), newPassword);
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        if (username == null || username.isBlank()) {
            throw new UsernameNotFoundException(
                langUtil.getMessage("error.user.username.empty"));
        }

        Optional<UserEntity> optionalUserEntity = this.repository.findByUsername(username);

        if (optionalUserEntity.isEmpty()) {
            log.error("Tried to load user with username {}, but user with this username doesn't exist", username);

            throw new UsernameNotFoundException(
                langUtil.getMessage("error.user.notFound.byUsername", username));
        }

        log.info("Loaded user with username {}", username);

        return optionalUserEntity.get();
    }

    @Override
    protected void fetchRelations(UserEntity entity, UserDTO dto) {
        throw new RuntimeException("Not yet implemented");
    }
}
