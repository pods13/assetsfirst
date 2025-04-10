package com.topably.assets.findata.splits.service;

import com.topably.assets.findata.dividends.domain.Dividend;
import com.topably.assets.findata.splits.domain.Split;
import com.topably.assets.findata.splits.repository.SplitRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SplitService {

    private final SplitRepository splitRepository;

    public List<Dividend> calculateDividendAdjustedValue(List<Dividend> dividends, Long instrumentId) {
        var splits = splitRepository.findAllByInstrument_IdOrderByExDate(instrumentId);
        if (CollectionUtils.isEmpty(splits)) {
            return dividends;
        }
        dividends.forEach(dividend -> {
            var relevantSplits = splits.stream().filter(s -> dividend.getRecordDate().isBefore(s.getExDate()))
                    .filter(s -> dividend.getLastSplitApplied() == null || dividend.getLastSplitApplied().isBefore(s.getExDate()));
            relevantSplits.forEach(split -> {
                var ratio = split.getRatio();
                var adjustedAmount = dividend.getAmount()
                        .multiply(BigDecimal.valueOf(ratio.getSecond()))
                        .divide(BigDecimal.valueOf(ratio.getFirst()), RoundingMode.HALF_EVEN);
                dividend.setUnadjustedAmount(dividend.getAmount());
                dividend.setAmount(adjustedAmount);
                dividend.setLastSplitApplied(split.getExDate());
            });
        });
        return dividends;
    }

    @Transactional(readOnly = true)
    public List<Split> findInstrumentsLastSplit() {
        return splitRepository.findInstrumentsLastSplit();
    }
}
