package pl.rychellos.hotel.fault;

import jakarta.persistence.*;
import java.util.UUID;
import lombok.*;
import pl.rychellos.hotel.lib.BaseEntity;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fault_type")
public class FaultTypeEntity implements BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Builder.Default
    @Column(unique = true, nullable = false)
    private UUID publicId = UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    private String description;
}
