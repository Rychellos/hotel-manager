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
import pl.rychellos.hotel.fault.FaultEntity;
import pl.rychellos.hotel.fault.FaultRepository;
import pl.rychellos.hotel.fault.FaultService;
import pl.rychellos.hotel.fault.dto.FaultDTO;
import pl.rychellos.hotel.fault.dto.FaultFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.JSONPatchDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationException;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

@Slf4j
@RestController
@RequestMapping("/api/v1/faults")
@Tag(name = "Faults", description = "Endpoints for managing faults")
public class FaultController
        extends GenericController<FaultEntity, FaultDTO, FaultFilterDTO, FaultRepository, FaultService> {
    public FaultController(
            FaultService service,
            ApplicationExceptionFactory applicationExceptionFactory,
            LangUtil langUtil) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "FAULT", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch all faults paginated")
    public Page<FaultDTO> getFaults(
            @Parameter(hidden = true) @PageableDefault(size = 50) Pageable pageable,
            @ParameterObject FaultFilterDTO filter) throws ApplicationException {
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{idOrUuid}")
    @CheckPermission(target = "FAULT", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch single fault by id or UUID")
    public ResponseEntity<FaultDTO> getById(@PathVariable String idOrUuid) throws ApplicationException {
        return ResponseEntity.ok(super.getOne(idOrUuid));
    }

    @PostMapping
    @CheckPermission(target = "FAULT", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Create new fault")
    public ResponseEntity<FaultDTO> create(@RequestBody FaultDTO dto) throws ApplicationException {
        return ResponseEntity.ok(super.createOne(dto));
    }

    @PutMapping("/{idOrUuid}")
    @CheckPermission(target = "FAULT", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Update fault")
    public ResponseEntity<FaultDTO> update(
            @PathVariable String idOrUuid,
            @RequestBody FaultDTO dto) throws ApplicationException {
        return ResponseEntity.ok(super.putOne(idOrUuid, dto));
    }

    @PatchMapping("/{idOrUuid}")
    @CheckPermission(target = "FAULT", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Patch fault")
    public ResponseEntity<FaultDTO> patch(
            @PathVariable String idOrUuid,
            @RequestBody JSONPatchDTO patch) throws ApplicationException {
        return ResponseEntity.ok(super.patchOne(idOrUuid, patch));
    }

    @DeleteMapping("/{idOrUuid}")
    @CheckPermission(target = "FAULT", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Delete fault")
    public ResponseEntity<Void> delete(@PathVariable String idOrUuid) throws ApplicationException {
        super.deleteOne(idOrUuid);
        return ResponseEntity.ok().build();
    }
}
