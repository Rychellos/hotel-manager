package pl.rychellos.hotel.webapi;

import com.github.fge.jsonpatch.JsonPatch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.authorization.user.UserService;
import pl.rychellos.hotel.authorization.user.dto.UserCreateDTO;
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
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController extends GenericController<
    UserEntity,
    UserDTO,
    UserFilterDTO,
    UserRepository,
    UserService
    > {
    public UserController(
        UserService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "USER", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch details of all users present.")
    public Page<UserDTO> getUsers(
        @Parameter(hidden = true)
        @PageableDefault(size = 50)
        Pageable pageable,
        @ParameterObject
        UserFilterDTO filter
    ) {
        log.info("Loading user detail table");
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{id}")
    @CheckPermission(target = "USER", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch details about single user")
    public ResponseEntity<UserDTO> getById(@PathVariable long id) {
        log.info("Loading detail about single user");

        return ResponseEntity.ok(super.getOne(id));
    }

    @PostMapping
    @CheckPermission(target = "USER", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Creates new user")
    public ResponseEntity<UserDTO> create(UserCreateDTO userCreateDTO) {
        log.info("Creating new user: {}", userCreateDTO.username());

        var userDTO = new UserDTO();
        userDTO.setUsername(userCreateDTO.username());
        userDTO.setEmail(userCreateDTO.email());
        userDTO.setRoleIds(userCreateDTO.roleIds());

        userDTO = this.createOne(userDTO);
        this.service.updatePassword(userDTO, userCreateDTO.password());

        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{id}")
    @CheckPermission(target = "USER", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Sets user details")
    public ResponseEntity<UserDTO> put(
        @PathVariable Long id,
        @RequestBody UserDTO userDTO
    ) {
        userDTO.setId(id);
        return ResponseEntity.ok(this.putOne(userDTO));
    }

    @PatchMapping("/{id}")
    @CheckPermission(target = "USER", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Updates user details")
    public ResponseEntity<UserDTO> patch(
        @PathVariable Long id,
        @RequestBody JsonPatch userDTO
    ) {
        return ResponseEntity.ok(this.patchOne(id, userDTO));
    }

    @DeleteMapping("/{id}")
    @CheckPermission(target = "USER", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Deletes user")
    public ResponseEntity<Void> delete(
        @PathVariable Long id
    ) {
        this.deleteOne(id);
        return ResponseEntity.ok().build();
    }
}
