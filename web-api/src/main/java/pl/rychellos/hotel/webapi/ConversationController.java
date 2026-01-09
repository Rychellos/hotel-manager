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
import pl.rychellos.hotel.conversation.ConversationEntity;
import pl.rychellos.hotel.conversation.ConversationRepository;
import pl.rychellos.hotel.conversation.ConversationService;
import pl.rychellos.hotel.conversation.MessageService;
import pl.rychellos.hotel.conversation.dto.ConversationDTO;
import pl.rychellos.hotel.conversation.dto.ConversationFilterDTO;
import pl.rychellos.hotel.conversation.dto.MessageDTO;
import pl.rychellos.hotel.conversation.dto.MessageFilterDTO;
import pl.rychellos.hotel.lib.GenericController;
import pl.rychellos.hotel.lib.exceptions.ApplicationExceptionFactory;
import pl.rychellos.hotel.lib.lang.LangUtil;
import pl.rychellos.hotel.lib.security.ActionScope;
import pl.rychellos.hotel.lib.security.ActionType;

import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/api/v1/conversations")
@Tag(name = "Conversations", description = "Endpoints for managing conversations and messages")
public class ConversationController extends GenericController<
    ConversationEntity,
    ConversationDTO,
    ConversationFilterDTO,
    ConversationRepository,
    ConversationService
    > {
    private final MessageService messageService;

    public ConversationController(
        ConversationService service,
        ApplicationExceptionFactory applicationExceptionFactory,
        LangUtil langUtil,
        MessageService messageService
    ) {
        super(service, applicationExceptionFactory, langUtil);
        this.messageService = messageService;
    }

    @GetMapping
    @PageableAsQueryParam
    @CheckPermission(target = "CONVERSATION", action = ActionType.READ, scope = ActionScope.PAGINATED)
    @Operation(summary = "Fetch all conversations paginated")
    public Page<ConversationDTO> getConversations(
        @Parameter(hidden = true)
        @PageableDefault(size = 50)
        Pageable pageable,
        @ParameterObject
        ConversationFilterDTO filter
    ) {
        return super.getPage(pageable, filter);
    }

    @GetMapping("/{idOrUuid}")
    @CheckPermission(target = "CONVERSATION", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch single conversation by id or UUID")
    public ResponseEntity<ConversationDTO> getById(@PathVariable String idOrUuid) {
        return ResponseEntity.ok(super.getOne(idOrUuid));
    }

    @PostMapping
    @CheckPermission(target = "CONVERSATION", action = ActionType.CREATE, scope = ActionScope.ONE)
    @Operation(summary = "Create new conversation")
    public ResponseEntity<ConversationDTO> create(@RequestBody ConversationDTO dto) {
        if (dto.getLastActivity() == null) {
            dto.setLastActivity(LocalDateTime.now());
        }

        return ResponseEntity.ok(super.createOne(dto));
    }

    @PutMapping("/{idOrUuid}")
    @CheckPermission(target = "CONVERSATION", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Update conversation")
    public ResponseEntity<ConversationDTO> update(
        @PathVariable
        String idOrUuid,
        @RequestBody
        ConversationDTO dto
    ) {
        return ResponseEntity.ok(super.putOne(idOrUuid, dto));
    }

    @PatchMapping("/{idOrUuid}")
    @CheckPermission(target = "CONVERSATION", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Patch conversation")
    public ResponseEntity<ConversationDTO> patch(
        @PathVariable
        String idOrUuid,
        @RequestBody
        JsonPatch patch
    ) {
        return ResponseEntity.ok(super.patchOne(idOrUuid, patch));
    }

    @DeleteMapping("/{idOrUuid}")
    @CheckPermission(target = "CONVERSATION", action = ActionType.DELETE, scope = ActionScope.ONE)
    @Operation(summary = "Delete conversation")
    public ResponseEntity<Void> delete(@PathVariable String idOrUuid) {
        super.deleteOne(idOrUuid);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{idOrUuid}/messages")
    @PageableAsQueryParam
    @CheckPermission(target = "CONVERSATION", action = ActionType.READ, scope = ActionScope.ONE)
    @Operation(summary = "Fetch messages for conversation")
    public Page<MessageDTO> getMessages(
        @Parameter(hidden = true)
        @PageableDefault(size = 50)
        Pageable pageable,
        @PathVariable
        String idOrUuid
    ) {
        MessageFilterDTO filter = new MessageFilterDTO();
        filter.setConversationId(resolveId(idOrUuid));
        return messageService.getAllPaginated(pageable, filter);
    }

    @PostMapping("/{idOrUuid}/messages")
    @CheckPermission(target = "CONVERSATION", action = ActionType.EDIT, scope = ActionScope.ONE)
    @Operation(summary = "Send message to conversation")
    public ResponseEntity<MessageDTO> sendMessage(
        @PathVariable String idOrUuid,
        @RequestBody MessageDTO dto
    ) {
        dto.setConversationId(resolveId(idOrUuid));
        if (dto.getSentTime() == null) {
            dto.setSentTime(LocalDateTime.now());
        }

        return ResponseEntity.ok(messageService.save(dto));
    }
}
