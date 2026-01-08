package pl.rychellos.hotel.conversation.dto;

import lombok.Data;
import pl.rychellos.hotel.conversation.ConversationType;
import pl.rychellos.hotel.lib.SearchFilter;

@Data
public class ConversationFilterDTO {
    @SearchFilter
    private ConversationType type;
}
