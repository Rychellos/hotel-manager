package pl.rychellos.hotel.room;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.rychellos.hotel.lib.BaseEntity;

import java.math.BigDecimal;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "room")
public class RoomEntity implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private java.util.UUID publicId = java.util.UUID.randomUUID();

    @Column(nullable = false)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "standard_id", nullable = false)
    private StandardEntity standard;

    @Column(nullable = false)
    private Integer bedsAvailable;

    private String roomDescription;

    private BigDecimal basePriceOverride;

    private BigDecimal perPersonPriceOverride;
}
