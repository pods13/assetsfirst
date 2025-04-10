package com.topably.assets.findata.splits.domain;

import com.topably.assets.instruments.domain.Instrument;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.data.util.Pair;

import java.math.BigDecimal;
import java.time.LocalDate;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Accessors(chain = true)
@Entity
@Table(name = "split")
public class Split {

    private static final String RATIO_DELIMITER = ":";

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO, generator = "native")
    @GenericGenerator(name = "native")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "instrument_id", referencedColumnName = "id",
            foreignKey = @ForeignKey(name = "fk__dividend__instrument_id__instrument"))
    private Instrument instrument;

    @Column(name = "payable_on")
    private LocalDate payableOn;

    @Column(name = "ex_date")
    private LocalDate exDate;

    @Column(name = "announced")
    private LocalDate announced;

    @Column(name = "ratio")
    // A:B A shares for every B shares held
    private String ratio;

    public Pair<Long, Long> getRatio() {
        String[] ab = this.ratio.split(RATIO_DELIMITER);
        return Pair.of(Long.parseLong(ab[0]), Long.parseLong(ab[1]));
    }
}
