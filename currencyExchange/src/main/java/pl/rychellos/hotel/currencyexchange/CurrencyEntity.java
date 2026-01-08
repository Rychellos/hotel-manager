package pl.rychellos.hotel.currencyexchange;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.rychellos.hotel.lib.BaseEntity;

import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "currency")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyEntity implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private UUID publicId = java.util.UUID.randomUUID();

    private String currency;
    private String code;

    private String no;
    private LocalDate effectiveDate;
    private Double mid;
}
