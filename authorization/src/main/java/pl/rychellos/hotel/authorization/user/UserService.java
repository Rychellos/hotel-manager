package pl.rychellos.hotel.authorization.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.user.dto.UserDTO;
import pl.rychellos.hotel.authorization.user.dto.UserFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Service
public class UserService extends GenericService<UserEntity, UserDTO, UserFilterDTO> {
    public UserService(
        UserRepository repository,
        UserMapper mapper,
        ApplicationExceptionFactory exceptionFactory,
        LangUtil langUtil,
        ObjectMapper objectMapper) {
        super(langUtil, UserDTO.class, mapper, repository, exceptionFactory, objectMapper);
    }
}
