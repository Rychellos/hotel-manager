package pl.rychellos.hotel.conversation.dto;

import lombok.Data;
import pl.rychellos.hotel.lib.SearchFilter;

@Data
public class MessageFilterDTO {
    @SearchFilter(path = "conversation.id")
    private Long conversationId;

    @SearchFilter(path = "author.id")
    private Long authorId;
}
