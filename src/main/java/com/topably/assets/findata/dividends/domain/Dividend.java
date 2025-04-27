package com.topably.assets.findata.dividends.domain;

import com.topably.assets.instruments.domain.Instrument;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "dividend", uniqueConstraints = {
        @UniqueConstraint(name = "uq_dividend_instrument_id_record_date", columnNames = {"instrument_id", "record_date"}),
})
public class Dividend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk__dividend__instrument_id__instrument"))
    private Instrument instrument;

    @Column(name = "declare_date")
    private LocalDate declareDate;

    @Column(name = "record_date")
    private LocalDate recordDate;

    @Column(name = "pay_date")
    private LocalDate payDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "unadjusted_amount")
    private BigDecimal unadjustedAmount;

    @Column(name = "last_split_applied")
    private LocalDate lastSplitApplied;
}
