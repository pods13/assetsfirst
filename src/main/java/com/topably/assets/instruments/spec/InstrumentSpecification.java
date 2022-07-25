package com.topably.assets.instruments.spec;

import com.topably.assets.instruments.domain.Instrument;
import com.topably.assets.instruments.domain.InstrumentType;
import net.kaczmarzyk.spring.data.jpa.domain.In;
import net.kaczmarzyk.spring.data.jpa.domain.LikeIgnoreCase;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Or;
import net.kaczmarzyk.spring.data.jpa.web.annotation.Spec;
import org.springframework.data.jpa.domain.Specification;

@Or({
        @Spec(path = "ticker", params = "search", spec = LikeIgnoreCase.class),
        @Spec(path = "instrumentType", params = "instrumentTypes", paramSeparator = ',', spec = In.class)
})
public interface InstrumentSpecification extends Specification<Instrument> {
}
