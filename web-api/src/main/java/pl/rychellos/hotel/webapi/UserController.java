package pl.rychellos.hotel.webapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.security.Principal;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springdoc.core.converters.models.PageableAsQueryParam;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.role.RoleService;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.authorization.user.UserRepository;
import pl.rychellos.hotel.authorization.user.UserService;
import pl.rychellos.hotel.authorization.user.dto.UserCreateDTO;
import pl.rychellos.hotel.authorization.user.dto.UserDTO;
import pl.rychellos.hotel.authorization.user.dto.UserFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.JSONPatchDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for managing users")
public class UserController extends GenericController<UserEntity, UserDTO, UserFilterDTO, UserRepository, UserService> {
    private final RoleService roleService;

    public UserController(
            UserService service,
            ApplicationExceptionFactory applicationExceptionFactory,
            LangUtil langUtil,
            RoleService roleService) {
        super(service, applicationExceptionFactory, langUtil);
        this.roleService = roleService;
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "USER", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch details of all users present.")
    public Page<UserDTO> getUsers(
            @Parameter(hidden = true) @PageableDefault(size = 50) Pageable pageable,
            @ParameterObject UserFilterDTO filter) throws ApplicationException {
        log.info("Loading user detail table");
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{idOrUuid}")
    @CheckPermission(target = "USER", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch details about single user by id or UUID")
    public ResponseEntity<UserDTO> getById(@PathVariable String idOrUuid) throws ApplicationException {
        log.info("Loading detail about single user");

        return ResponseEntity.ok(super.getOne(idOrUuid));
    }

    @GetMapping("/{idOrUuid}/roles")
    @CheckPermission(target = "USER", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch list of roles that single user have")
    public ResponseEntity<List<RoleDTO>> getRolesById(@PathVariable String idOrUuid) throws ApplicationException {
        log.info("Loading list of roles for single user");

        List<RoleDTO> roles = new java.util.ArrayList<>();
        for (Long id : super.getOne(idOrUuid).getRoleIds()) {
            roles.add(roleService.getById(id));
        }

        return ResponseEntity.ok(roles);
    }

    @GetMapping("/{idOrUuid}/permissions")
    @CheckPermission(target = "USER", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch list of permission that single user have")
    public ResponseEntity<List<PermissionDTO>> getPermissionsById(@PathVariable String idOrUuid)
            throws ApplicationException {
        log.info("Loading list of permissions for single user");

        return ResponseEntity.ok(service.getPermissions(this.resolveId(idOrUuid)));
    }

    @PostMapping
    @CheckPermission(target = "USER", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Creates new user")
    public ResponseEntity<UserDTO> create(UserCreateDTO userCreateDTO) throws ApplicationException {
        log.info("Creating new user: {}", userCreateDTO.getUsername());

        var userDTO = new UserDTO();
        userDTO.setUsername(userCreateDTO.getUsername());
        userDTO.setEmail(userCreateDTO.getEmail());
        userDTO.setRoleIds(userCreateDTO.getRoleIds());

        userDTO = this.createOne(userDTO);
        this.service.updatePassword(userDTO, userCreateDTO.getPassword());

        return ResponseEntity.ok(userDTO);
    }

    @PutMapping("/{idOrUuid}")
    @CheckPermission(target = "USER", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Sets user details")
    public ResponseEntity<UserDTO> put(
            @PathVariable String idOrUuid,
            @RequestBody UserDTO userDTO) throws ApplicationException {
        return ResponseEntity.ok(this.putOne(idOrUuid, userDTO));
    }

    @PatchMapping("/{idOrUuid}")
    @CheckPermission(target = "USER", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Updates user details")
    public ResponseEntity<UserDTO> patch(
            @PathVariable String idOrUuid,
            @RequestBody JSONPatchDTO userDTO) throws ApplicationException {
        return ResponseEntity.ok(this.patchOne(idOrUuid, userDTO));
    }

    @DeleteMapping("/{idOrUuid}")
    @CheckPermission(target = "USER", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Deletes user")
    public ResponseEntity<Void> delete(@PathVariable String idOrUuid) throws ApplicationException {
        this.deleteOne(idOrUuid);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    @CheckPermission(target = "USER", action = ActionType.READ, scope = ActionScope.SELF)
    @Operation(summary = "Fetches information about currently logged in user")
    public ResponseEntity<UserDTO> me(
            Principal principal) throws ApplicationException {
        return ResponseEntity.ok(service.getByUsername(principal.getName()));
    }
}
