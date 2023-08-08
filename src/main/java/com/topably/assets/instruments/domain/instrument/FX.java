package com.topably.assets.instruments.domain.instrument;

import com.topably.assets.instruments.domain.Instrument;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("FX")
@EqualsAndHashCode(callSuper = true)
public class FX extends Instrument {
}
