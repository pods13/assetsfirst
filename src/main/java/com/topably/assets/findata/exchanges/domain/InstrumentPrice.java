package com.topably.assets.findata.exchanges.domain;

import com.topably.assets.instruments.domain.Instrument;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class InstrumentPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk__instrument_price__instrument_id__instrument"))
    private Instrument instrument;

    private LocalDateTime datetime;

    private BigDecimal value;
}
