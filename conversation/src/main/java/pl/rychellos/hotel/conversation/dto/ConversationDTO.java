package pl.rychellos.hotel.conversation.dto;

import lombok.Data;
import pl.rychellos.hotel.conversation.ConversationType;
import pl.rychellos.hotel.lib.BaseDTO;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class ConversationDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private Set<Long> participantIds;
    private LocalDateTime lastActivity;
    private ConversationType type;
}
