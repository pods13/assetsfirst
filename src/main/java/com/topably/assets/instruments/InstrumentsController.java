package com.topably.assets.instruments;

import com.topably.assets.instruments.domain.InstrumentType;
import com.topably.assets.instruments.domain.dto.ImportInstrumentDto;
import com.topably.assets.instruments.domain.dto.InstrumentDto;
import com.topably.assets.instruments.service.InstrumentService;
import com.topably.assets.instruments.service.importer.DefaultInstrumentImporter;
import com.topably.assets.instruments.spec.InstrumentSpecification;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/instruments")
@RequiredArgsConstructor
public class InstrumentsController {

    private final InstrumentService instrumentService;
    private final DefaultInstrumentImporter importer;

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

    @PutMapping("/import")
    public void importInstruments(@Valid @RequestBody ImportInstrumentDto instrument) {
        importer.importInstrument(instrument);
    }
}
