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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.rychellos.hotel.authorization.annotation.CheckPermission;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.JSONPatchDTO;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;
import pl.rychellos.hotel.media.MediaEntity;
import pl.rychellos.hotel.media.MediaRepository;
import pl.rychellos.hotel.media.MediaService;
import pl.rychellos.hotel.media.dto.MediaDTO;
import pl.rychellos.hotel.media.dto.MediaFilterDTO;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/api/v1/media")
@Tag(name = "Media", description = "Endpoints for managing media files and images")
public class MediaController extends GenericController<
    MediaEntity,
    MediaDTO,
    MediaFilterDTO,
    MediaRepository,
    MediaService
    > {
    public MediaController(
        MediaService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil
    ) {
        super(service, applicationExceptionFactory, langUtil);
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "MEDIA", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch all media paginated")
    public Page<MediaDTO> getMedia(
        @Parameter(hidden = true)
        @PageableDefault(size = 50)
        Pageable pageable,
        @ParameterObject
        MediaFilterDTO filter
    ) {
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{idOrUuid}")
    @CheckPermission(target = "MEDIA", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch single media by id or UUID")
    public ResponseEntity<MediaDTO> getById(@PathVariable String idOrUuid) {
        return ResponseEntity.ok(super.getOne(idOrUuid));
    }

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @CheckPermission(target = "MEDIA", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Upload a new media file (Max 25MB)")
    public ResponseEntity<MediaDTO> upload(
        @RequestParam("file")
        MultipartFile file,
        @RequestParam(value = "ownerId", required = false)
        Long ownerId,
        @RequestParam(value = "isPublic", defaultValue = "false")
        boolean isPublic
    ) throws IOException {
        return ResponseEntity.ok(service.upload(file, ownerId, isPublic));
    }

    @PutMapping("/{idOrUuid}")
    @CheckPermission(target = "MEDIA", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Update media metadata")
    public ResponseEntity<MediaDTO> update(
        @PathVariable String idOrUuid,
        @RequestBody MediaDTO dto
    ) {
        return ResponseEntity.ok(super.putOne(idOrUuid, dto));
    }

    @PatchMapping("/{idOrUuid}")
    @CheckPermission(target = "MEDIA", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Patch media metadata")
    public ResponseEntity<MediaDTO> patch(
        @PathVariable String idOrUuid,
        @RequestBody JSONPatchDTO patch
    ) {
        return ResponseEntity.ok(super.patchOne(idOrUuid, patch));
    }

    @DeleteMapping("/{idOrUuid}")
    @CheckPermission(target = "MEDIA", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Delete media")
    public ResponseEntity<Void> delete(@PathVariable String idOrUuid) {
        super.deleteOne(idOrUuid);
        return ResponseEntity.ok().build();
    }
}
