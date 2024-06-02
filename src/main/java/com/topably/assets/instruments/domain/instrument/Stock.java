package com.topably.assets.instruments.domain.instrument;

import com.topably.assets.instruments.domain.Instrument;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;


@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("STOCK")
@EqualsAndHashCode(callSuper = true)
public class Stock extends Instrument {
}
