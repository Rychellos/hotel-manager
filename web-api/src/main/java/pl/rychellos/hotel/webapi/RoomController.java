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
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;
import pl.rychellos.hotel.room.RoomEntity;
import pl.rychellos.hotel.room.RoomRepository;
import pl.rychellos.hotel.room.RoomService;
import pl.rychellos.hotel.room.dto.RoomDTO;
import pl.rychellos.hotel.room.dto.RoomFilterDTO;

@Slf4j
@RestController
@RequestMapping("/api/v1/rooms")
@Tag(name = "Rooms", description = "Endpoints for managing rooms")
public class RoomController extends GenericController<
    RoomEntity,
    RoomDTO,
    RoomFilterDTO,
    RoomRepository,
    RoomService
    > {
    public RoomController(
        RoomService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "ROOM", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch all rooms paginated")
    public Page<RoomDTO> getRooms(
        @Parameter(hidden = true)
        @PageableDefault(size = 50)
        Pageable pageable,
        @ParameterObject
        RoomFilterDTO filter
    ) {
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{idOrUuid}")
    @CheckPermission(target = "ROOM", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch single room by id or UUID")
    public ResponseEntity<RoomDTO> getById(@PathVariable String idOrUuid) {
        return ResponseEntity.ok(super.getOne(idOrUuid));
    }

    @PostMapping
    @CheckPermission(target = "ROOM", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Create new room")
    public ResponseEntity<RoomDTO> create(@RequestBody RoomDTO dto) {
        return ResponseEntity.ok(super.createOne(dto));
    }

    @PutMapping("/{idOrUuid}")
    @CheckPermission(target = "ROOM", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Update room")
    public ResponseEntity<RoomDTO> update(
        @PathVariable String idOrUuid,
        @RequestBody RoomDTO dto
    ) {
        return ResponseEntity.ok(super.putOne(idOrUuid, dto));
    }

    @PatchMapping("/{idOrUuid}")
    @CheckPermission(target = "ROOM", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Patch room")
    public ResponseEntity<RoomDTO> patch(
        @PathVariable String idOrUuid,
        @RequestBody JsonPatch patch
    ) {
        return ResponseEntity.ok(super.patchOne(idOrUuid, patch));
    }

    @DeleteMapping("/{idOrUuid}")
    @CheckPermission(target = "ROOM", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Delete room")
    public ResponseEntity<Void> delete(@PathVariable String idOrUuid) {
        super.deleteOne(idOrUuid);
        return ResponseEntity.ok().build();
    }
}
