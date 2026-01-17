package pl.rychellos.hotel.webapi;

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
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.role.RoleRepository;
import pl.rychellos.hotel.authorization.role.RoleService;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.authorization.role.dto.RoleFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.JSONPatchDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles", description = "Endpoints for managing roles")
@Slf4j
public class RoleController extends GenericController<
    RoleEntity,
    RoleDTO,
    RoleFilterDTO,
    RoleRepository,
    RoleService
    > {

    protected RoleController(
        RoleService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "ROLE", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch details of all roles present")
    public Page<RoleDTO> getAll(
        @Parameter(hidden = true)
        @PageableDefault(size = 50)
        Pageable pageable,
        @ParameterObject
        RoleFilterDTO filter
    ) {
        return getPage(pageable, filter);
    }

    @GetMapping("/{idOrUuid}")
    @CheckPermission(target = "ROLE", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch details about single role by id or UUID")
    public RoleDTO getById(@PathVariable String idOrUuid) {
        return getOne(idOrUuid);
    }

    @GetMapping("/{idOrUuid}/permissions")
    @CheckPermission(target = "ROLE", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch list of permission for single role")
    public ResponseEntity<List<PermissionDTO>> getPermissionsById(@PathVariable String idOrUuid) {
        log.info("Loading list of permissions for single role");

        return ResponseEntity.ok(service.getPermissions(this.resolveId(idOrUuid)));
    }

    @PostMapping
    @CheckPermission(target = "ROLE", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Create new role")
    public ResponseEntity<RoleDTO> create(RoleDTO roleDTO) {
        return ResponseEntity.ok(this.createOne(roleDTO));
    }

    @PutMapping("/{idOrUuid}")
    @CheckPermission(target = "ROLE", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Sets role's details")
    public ResponseEntity<RoleDTO> put(
        @PathVariable String idOrUuid,
        @RequestBody RoleDTO roleDTO
    ) {
        return ResponseEntity.ok(this.putOne(idOrUuid, roleDTO));
    }

    @PatchMapping("/{idOrUuid}")
    @CheckPermission(target = "ROLE", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Updates role's details")
    public ResponseEntity<RoleDTO> patch(
        @PathVariable String idOrUuid,
        @RequestBody JSONPatchDTO patchDTO
    ) {
        log.info("Patching role with id: {}", idOrUuid);
        return ResponseEntity.ok(this.patchOne(idOrUuid, patchDTO));
    }

    @DeleteMapping("/{idOrUuid}")
    @CheckPermission(target = "ROLE", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Deletes role")
    public ResponseEntity<Void> delete(
        @PathVariable String idOrUuid
    ) {
        this.deleteOne(idOrUuid);
        return ResponseEntity.ok().build();
    }
}
