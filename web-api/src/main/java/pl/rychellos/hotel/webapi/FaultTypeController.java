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
import pl.rychellos.hotel.fault.FaultTypeEntity;
import pl.rychellos.hotel.fault.FaultTypeRepository;
import pl.rychellos.hotel.fault.FaultTypeService;
import pl.rychellos.hotel.fault.dto.FaultTypeDTO;
import pl.rychellos.hotel.fault.dto.FaultTypeFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

@Slf4j
@RestController
@RequestMapping("/api/v1/fault-types")
@Tag(name = "Fault Types", description = "Endpoints for managing fault types")
public class FaultTypeController extends GenericController<
    FaultTypeEntity,
    FaultTypeDTO,
    FaultTypeFilterDTO,
    FaultTypeRepository,
    FaultTypeService
    > {
    public FaultTypeController(
        FaultTypeService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "FAULT_TYPE", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch all fault types paginated")
    public Page<FaultTypeDTO> getFaultTypes(
        @Parameter(hidden = true)
        @PageableDefault(size = 50)
        Pageable pageable,
        @ParameterObject
        FaultTypeFilterDTO filter
    ) {
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{idOrUuid}")
    @CheckPermission(target = "FAULT_TYPE", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch single fault type by id or UUID")
    public ResponseEntity<FaultTypeDTO> getById(@PathVariable String idOrUuid) {
        return ResponseEntity.ok(super.getOne(idOrUuid));
    }

    @PostMapping
    @CheckPermission(target = "FAULT_TYPE", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Create new fault type")
    public ResponseEntity<FaultTypeDTO> create(@RequestBody FaultTypeDTO dto) {
        return ResponseEntity.ok(super.createOne(dto));
    }

    @PutMapping("/{idOrUuid}")
    @CheckPermission(target = "FAULT_TYPE", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Update fault type")
    public ResponseEntity<FaultTypeDTO> update(
        @PathVariable String idOrUuid,
        @RequestBody FaultTypeDTO dto
    ) {
        return ResponseEntity.ok(super.putOne(idOrUuid, dto));
    }

    @PatchMapping("/{idOrUuid}")
    @CheckPermission(target = "FAULT_TYPE", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Patch fault type")
    public ResponseEntity<FaultTypeDTO> patch(
        @PathVariable String idOrUuid,
        @RequestBody JsonPatch patch) {
        return ResponseEntity.ok(super.patchOne(idOrUuid, patch));
    }

    @DeleteMapping("/{idOrUuid}")
    @CheckPermission(target = "FAULT_TYPE", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Delete fault type")
    public ResponseEntity<Void> delete(@PathVariable String idOrUuid) {
        super.deleteOne(idOrUuid);
        return ResponseEntity.ok().build();
    }
}
