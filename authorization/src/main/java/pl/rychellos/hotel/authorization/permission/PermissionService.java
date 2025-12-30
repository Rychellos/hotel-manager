package pl.rychellos.hotel.authorization.permission;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.permission.dto.PermissionFilterDTO;
import pl.rychellos.hotel.lib.GenericService;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;

@Service
public class PermissionService extends GenericService<PermissionEntity, PermissionDTO, PermissionFilterDTO> {
    public PermissionService(
        LangUtil languageUtil,
        PermissionMapper mapper,
        PermissionRepository repository,
        ApplicationExceptionFactory exceptionFactory,
        ObjectMapper objectMapper
    ) {
        super(languageUtil, PermissionDTO.class, mapper, repository, exceptionFactory, objectMapper);
    }
}
