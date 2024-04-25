package com.topably.assets.findata.dividends;

import java.util.List;

import com.topably.assets.findata.dividends.domain.dto.PubDividendDto;
import com.topably.assets.findata.dividends.service.DividendService;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/public/dividends")
@RequiredArgsConstructor
@Validated
public class PubDividendsController {

    private final DividendService dividendService;

    @GetMapping("/{ticker}")
    public List<PubDividendDto> findDividends(@PathVariable @NotBlank String ticker) {
        return dividendService.findDividendsByTicker(ticker);
    }
}
