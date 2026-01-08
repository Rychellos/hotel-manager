package pl.rychellos.hotel.webapi;

import com.github.fge.jsonpatch.JsonPatch;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.authorization.role.RoleEntity;
import pl.rychellos.hotel.authorization.role.RoleRepository;
import pl.rychellos.hotel.authorization.role.RoleService;
import pl.rychellos.hotel.authorization.role.dto.RoleDTO;
import pl.rychellos.hotel.authorization.role.dto.RoleFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

@RestController
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles", description = "Endpoints for managing roles")
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
    @CheckPermission(target = "ROLE", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch details of all roles present")
    public Page<RoleDTO> getAll(Pageable pageable, RoleFilterDTO filter) {
        return getPage(pageable, filter);
    }

    @GetMapping("/{id}")
    @CheckPermission(target = "ROLE", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch details about single role")
    public RoleDTO getById(@PathVariable long id) {
        return getOne(id);
    }

    @PostMapping
    @CheckPermission(target = "ROLE", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Create new role")
    public ResponseEntity<RoleDTO> create(RoleDTO roleDTO) {
        return ResponseEntity.ok(this.createOne(roleDTO));
    }

    @PutMapping("/{id}")
    @CheckPermission(target = "ROLE", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Sets role's details")
    public ResponseEntity<RoleDTO> put(
        @PathVariable Long id,
        @RequestBody RoleDTO roleDTO
    ) {
        roleDTO.setId(id);
        return ResponseEntity.ok(this.putOne(roleDTO));
    }

    @PatchMapping("/{id}")
    @CheckPermission(target = "ROLE", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Updates role's details")
    public ResponseEntity<RoleDTO> patch(
        @PathVariable Long id,
        @RequestBody JsonPatch roleDTO
    ) {
        return ResponseEntity.ok(this.patchOne(id, roleDTO));
    }

    @DeleteMapping("/{id}")
    @CheckPermission(target = "ROLE", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Deletes role")
    public ResponseEntity<Void> delete(
        @PathVariable Long id
    ) {
        this.deleteOne(id);
        return ResponseEntity.ok().build();
    }
}
