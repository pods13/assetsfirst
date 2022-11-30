package com.topably.assets.dividends.domain;

import com.topably.assets.instruments.domain.Instrument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ForeignKey;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
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
}
