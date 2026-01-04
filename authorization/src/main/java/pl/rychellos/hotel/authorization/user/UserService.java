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
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

import java.util.Optional;

@Slf4j
@Service
public class UserService extends GenericService<UserEntity, UserDTO, UserFilterDTO, UserRepository> implements UserDetailsPasswordService, UserDetailsService {
    public UserService(
        UserRepository repository,
        UserMapper mapper,
        ApplicationExceptionFactory exceptionFactory,
        LangUtil langUtil,
        ObjectMapper objectMapper
    ) {
        super(langUtil, UserDTO.class, mapper, repository, exceptionFactory, objectMapper);
    }

    @Override
    public UserDetails updatePassword(@NonNull UserDetails user, @Nullable String newPassword) {
        Optional<UserEntity> optionalUserEntity = this.repository.findByUsername(user.getUsername());

        if (optionalUserEntity.isEmpty()) {
            log.info("Tried to change password of user with username {}, but user with this username doesn't exist", user.getUsername());

            throw exceptionFactory.resourceNotFound(
                langUtil.getMessage("error.user.notFound.byUsername", user.getUsername())
            );
        }

        log.info("Changed {}'s password", user.getUsername());

        UserEntity userEntity = optionalUserEntity.get();

        userEntity.setPassword(newPassword);

        return this.repository.save(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(@NonNull String username) throws UsernameNotFoundException {
        Optional<UserEntity> optionalUserEntity = this.repository.findByUsername(username);

        if (optionalUserEntity.isEmpty()) {
            log.error("Tried to load user with username {}, but user with this username doesn't exist", username);

            throw exceptionFactory.resourceNotFound(
                langUtil.getMessage("error.user.notFound.byUsername", username)
            );
        }

        log.info("Loaded user with username {}", username);

        return optionalUserEntity.get();
    }

    @Override
    protected void fetchRelations(UserEntity entity, UserDTO dto) {
        throw new RuntimeException("Not yet implemented");
    }
}
