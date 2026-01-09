package pl.rychellos.hotel.conversation;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.rychellos.hotel.authorization.user.UserEntity;
import pl.rychellos.hotel.lib.BaseEntity;
import pl.rychellos.hotel.media.MediaEntity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "message")
public class MessageEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID publicId = UUID.randomUUID();

    @Column(nullable = false, columnDefinition = "TEXT")
    private String textContent;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "message_media",
        joinColumns = @JoinColumn(name = "message_id"),
        inverseJoinColumns = @JoinColumn(name = "media_id")
    )
    private Set<MediaEntity> mediaContent = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "conversation_id", nullable = false)
    private ConversationEntity conversation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserEntity author;

    @Column(nullable = false)
    private LocalDateTime sentTime;
}
