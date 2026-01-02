package pl.rychellos.hotel.authorization.role;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.authorization.role.dto.RoleFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Service
public class RoleService extends GenericService<RoleEntity, RoleDTO, RoleFilterDTO, RoleRepository> {
    public RoleService(
        RoleRepository repository,
        RoleMapper mapper,
        ApplicationExceptionFactory exceptionFactory,
        LangUtil languageUtil,
        ObjectMapper objectMapper) {
        super(languageUtil, RoleDTO.class, mapper, repository, exceptionFactory, objectMapper);
    }
}
