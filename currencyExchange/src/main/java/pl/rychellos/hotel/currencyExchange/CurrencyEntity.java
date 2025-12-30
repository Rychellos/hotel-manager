package pl.rychellos.hotel.currencyExchange;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import pl.rychellos.hotel.lib.BaseEntity;

import java.time.LocalDate;

@Entity
@Table(name = "currency")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CurrencyEntity implements BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String currency;
    private String code;

    private String no;
    private LocalDate effectiveDate;
    private Double mid;
}

