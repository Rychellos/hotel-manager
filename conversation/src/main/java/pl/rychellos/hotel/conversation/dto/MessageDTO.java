package pl.rychellos.hotel.conversation.dto;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.Data;
import pl.rychellos.hotel.lib.BaseDTO;

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
