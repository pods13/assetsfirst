package com.topably.assets.findata.dividends;

import com.topably.assets.findata.dividends.domain.dto.DividendData;
import com.topably.assets.findata.dividends.service.DividendService;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

@RestController
@RequestMapping("/dividends")
@RequiredArgsConstructor
public class DividendsController {

    private final DividendService dividendService;

    @PostMapping
    public void addDividends(@RequestParam String ticker, @RequestParam String exchange,
                             @Validated @RequestBody Collection<DividendData> dividendData) {
        dividendService.addDividends(ticker, exchange, dividendData);
    }
}
