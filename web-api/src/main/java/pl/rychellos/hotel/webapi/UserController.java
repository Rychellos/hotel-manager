package pl.rychellos.hotel.webapi;

import io.swagger.v3.oas.annotations.Parameter;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.authorization.user.UserService;
import pl.rychellos.hotel.authorization.user.dto.UserDTO;
import pl.rychellos.hotel.authorization.user.dto.UserFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
public class UserController extends GenericController<UserEntity, UserDTO, UserFilterDTO, UserRepository> {
    protected UserController(
        UserService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "USER", action = ActionType.READ, scope = ActionScope.PAGINATED)
    public Page<UserDTO> getUsers(
        @Parameter(hidden = true)
        @PageableDefault(size = 50)
        Pageable pageable,
        @ParameterObject
        UserFilterDTO filter
    ) {
        log.info("Loading user table list");
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{id}")
    @CheckPermission(target = "USER", action = ActionType.READ, scope = ActionScope.ONE)
    public UserDTO getById(@PathVariable long id) {
        return super.getOne(id);
    }
}
