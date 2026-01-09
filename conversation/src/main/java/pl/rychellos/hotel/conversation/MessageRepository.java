package pl.rychellos.hotel.conversation;

import org.springframework.stereotype.Repository;
import pl.rychellos.hotel.lib.GenericRepository;

@Repository
public interface MessageRepository extends GenericRepository<MessageEntity> {
}
