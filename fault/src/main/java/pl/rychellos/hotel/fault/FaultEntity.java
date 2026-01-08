package pl.rychellos.hotel.fault;

import jakarta.persistence.*;
import lombok.*;
import pl.rychellos.hotel.conversation.ConversationEntity;
import pl.rychellos.hotel.lib.BaseEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fault")
public class FaultEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(unique = true, nullable = false)
    private UUID publicId = UUID.randomUUID();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fault_type_id", nullable = false)
    private FaultTypeEntity faultType;

    @Column(nullable = false)
    private Long reporterId;

    private Long repairmanAssignedId;

    @Column(nullable = false)
    private LocalDateTime reported;

    private LocalDateTime repaired;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "conversation_id")
    private ConversationEntity conversation;
}
