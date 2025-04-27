package com.topably.assets.findata.xrates.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Currency;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "exchange_rate", uniqueConstraints = {
        @UniqueConstraint(name = "uq_exchange_rate", columnNames = {"source_currency", "destination_currency", "date"}),
})
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
