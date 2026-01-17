package pl.rychellos.hotel.webapi;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.authorization.permission.PermissionEntity;
import pl.rychellos.hotel.authorization.permission.PermissionRepository;
import pl.rychellos.hotel.authorization.permission.PermissionService;
import pl.rychellos.hotel.authorization.permission.dto.PermissionDTO;
import pl.rychellos.hotel.authorization.permission.dto.PermissionFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.JSONPatchDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

@RestController
@RequestMapping("/api/v1/permissions")
@Tag(name = "Permission", description = "Endpoints for managing permissions")
public class PermissionController extends GenericController<
    PermissionEntity,
    PermissionDTO,
    PermissionFilterDTO,
    PermissionRepository,
    PermissionService
    > {
    protected PermissionController(
        PermissionService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @CheckPermission(target = "PERMISSION", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch details of all permissions present")
    public Page<PermissionDTO> getAll(
        Pageable pageable,
        PermissionFilterDTO filter) {
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{idOrUuid}")
    @CheckPermission(target = "PERMISSION", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch details about single permission by id or UUID")
    public PermissionDTO getById(@PathVariable String idOrUuid) {
        return super.getOne(idOrUuid);
    }

    @PostMapping
    @CheckPermission(target = "PERMISSION", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Create new permission")
    public ResponseEntity<PermissionDTO> create(PermissionDTO permissionDTO) {
        return ResponseEntity.ok(this.createOne(permissionDTO));
    }

    @PutMapping("/{idOrUuid}")
    @CheckPermission(target = "PERMISSION", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Sets permission's details")
    public ResponseEntity<PermissionDTO> put(
        @PathVariable String idOrUuid,
        @RequestBody PermissionDTO permissionDTO
    ) {
        return ResponseEntity.ok(this.putOne(idOrUuid, permissionDTO));
    }

    @PatchMapping("/{idOrUuid}")
    @CheckPermission(target = "PERMISSION", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Updates permission's details")
    public ResponseEntity<PermissionDTO> patch(
        @PathVariable String idOrUuid,
        @RequestBody JSONPatchDTO permissionDTO
    ) {
        return ResponseEntity.ok(this.patchOne(idOrUuid, permissionDTO));
    }

    @DeleteMapping("/{idOrUuid}")
    @CheckPermission(target = "PERMISSION", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Deletes permission")
    public ResponseEntity<Void> delete(
        @PathVariable String idOrUuid
    ) {
        this.deleteOne(idOrUuid);
        return ResponseEntity.ok().build();
    }
}
