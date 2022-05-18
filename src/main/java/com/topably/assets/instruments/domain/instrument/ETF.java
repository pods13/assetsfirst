package com.topably.assets.instruments.domain.instrument;

import com.topably.assets.instruments.domain.Instrument;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

@Setter
@Getter
@NoArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("ETF")
public class ETF extends Instrument {

    public static final String NAME_ATTRIBUTE = "name";

    public String getName() {
        return getAttributes().get(NAME_ATTRIBUTE);
    }
}
