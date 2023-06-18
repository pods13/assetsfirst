package com.topably.assets.instruments;

import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.dto.InstrumentDto;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.instruments.spec.InstrumentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/instruments")
@RequiredArgsConstructor
public class InstrumentsController {

    private final InstrumentService instrumentService;

    @GetMapping
    public Collection<InstrumentDto> findTradingInstruments(@RequestParam(required = false) String search,
                                                            @RequestParam(required = false) Collection<InstrumentType> instrumentTypes,
                                                            InstrumentSpecification specification) {
        //TODO add limit via pagination
        return instrumentService.searchTradingInstruments(specification);
    }

    @GetMapping("/{identifier}")
    public InstrumentDto findInstrumentByIdentifier(@PathVariable String identifier) {
        return instrumentService.findInstrumentByIdentifier(identifier);
    }
}
