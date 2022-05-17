package com.topably.assets.trades.domain;

import com.topably.assets.auth.domain.User;
import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.trades.domain.TradeOperation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Trade {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native", strategy = "native")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", referencedColumnName = "ID", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "INSTRUMENT_ID", referencedColumnName = "ID", nullable = false)
    private Instrument instrument;

    private LocalDateTime date;

    @Enumerated
    @Column(columnDefinition = "tinyint", nullable = false)
    private TradeOperation operation;

    private BigInteger quantity;
    private BigDecimal price;
}
