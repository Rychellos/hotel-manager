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
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.JSONPatchDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;
import pl.rychellos.hotel.room.StandardEntity;
import pl.rychellos.hotel.room.StandardRepository;
import pl.rychellos.hotel.room.StandardService;
import pl.rychellos.hotel.room.dto.StandardDTO;
import pl.rychellos.hotel.room.dto.StandardFilterDTO;

@Slf4j
@RestController
@RequestMapping("/api/v1/standards")
@Tag(name = "Standards", description = "Endpoints for managing room standards")
public class StandardController extends GenericController<
    StandardEntity,
    StandardDTO,
    StandardFilterDTO,
    StandardRepository,
    StandardService
    > {
    public StandardController(
        StandardService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "STANDARD", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch all standards paginated")
    public Page<StandardDTO> getStandards(
        @Parameter(hidden = true)
        @PageableDefault(size = 50)
        Pageable pageable,
        @ParameterObject StandardFilterDTO filter
    ) {
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{idOrUuid}")
    @CheckPermission(target = "STANDARD", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch single standard by id or UUID")
    public ResponseEntity<StandardDTO> getById(@PathVariable String idOrUuid) {
        return ResponseEntity.ok(super.getOne(idOrUuid));
    }

    @PostMapping
    @CheckPermission(target = "STANDARD", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Create new standard")
    public ResponseEntity<StandardDTO> create(@RequestBody StandardDTO dto) {
        return ResponseEntity.ok(super.createOne(dto));
    }

    @PutMapping("/{idOrUuid}")
    @CheckPermission(target = "STANDARD", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Update standard")
    public ResponseEntity<StandardDTO> update(
        @PathVariable String idOrUuid,
        @RequestBody StandardDTO dto
    ) {
        return ResponseEntity.ok(super.putOne(idOrUuid, dto));
    }

    @PatchMapping("/{idOrUuid}")
    @CheckPermission(target = "STANDARD", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Patch standard")
    public ResponseEntity<StandardDTO> patch(
        @PathVariable String idOrUuid,
        @RequestBody JSONPatchDTO patch
    ) {
        return ResponseEntity.ok(super.patchOne(idOrUuid, patch));
    }

    @DeleteMapping("/{idOrUuid}")
    @CheckPermission(target = "STANDARD", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Delete standard")
    public ResponseEntity<Void> delete(@PathVariable String idOrUuid) {
        super.deleteOne(idOrUuid);
        return ResponseEntity.ok().build();
    }
}
