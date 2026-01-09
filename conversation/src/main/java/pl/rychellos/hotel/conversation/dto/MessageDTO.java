package pl.rychellos.hotel.conversation.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Data
public class MessageDTO implements BaseDTO {
    private Long id;
    private UUID publicId;
    private String textContent;
    private Set<Long> mediaIds;
    private Long conversationId;
    private Long authorId;
    private LocalDateTime sentTime;
}
