package com.topably.assets.xrates.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "exchange_rate", uniqueConstraints = {
        @UniqueConstraint(name="uq_exchange_rate", columnNames = {"source_currency", "destination_currency", "date"}),
})
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @Column(name = "source_currency")
    private Currency sourceCurrency;
    @Column(name = "destination_currency")
    private Currency destinationCurrency;
    @Column(name = "conversion_rate")
    private BigDecimal conversionRate;

    @Column(name = "date")
    private LocalDate date;
}
