package com.topably.assets.portfolios.service.cards.producer;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.CardContainerType;
import com.topably.assets.portfolios.domain.cards.CardData;
import com.topably.assets.portfolios.domain.cards.input.BalanceCard;
import com.topably.assets.portfolios.domain.cards.output.BalanceCardData;
import com.topably.assets.portfolios.service.PortfolioService;
import com.topably.assets.portfolios.service.cards.CardStateProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Currency;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static com.topably.assets.portfolios.domain.cards.output.BalanceCardData.TimeFrameSummary;

@Service(CardContainerType.BALANCE)
@RequiredArgsConstructor
@Slf4j
public class BalanceCardStateProducer implements CardStateProducer<BalanceCard> {

    private final PortfolioService portfolioService;

    @Override
    public CardData produce(Portfolio portfolio, BalanceCard card) {
        return new BalanceCardData()
            .setInvestedAmount(portfolioService.calculateInvestedAmount(portfolio))
            .setCurrentAmount(portfolioService.calculateCurrentAmount(portfolio))
            //TODO use portfolio currency instead
            .setCurrencySymbol(Currency.getInstance("RUB").getSymbol())
            .setInvestedAmountByDates(getInvestedAmountByDates(portfolio));
    }

    private TimeFrameSummary getInvestedAmountByDates(Portfolio portfolio) {
        var endDate = LocalDate.now().plusDays(1);
        var start = endDate.minusYears(1);
        var datesBetween = getDatesBetween(start, endDate);
        var xAxis = datesBetween.stream()
            .map(Objects::toString)
            .toList();
        var values = datesBetween.stream()
            .map(d -> portfolioService.calculateInvestedAmountByDate(portfolio, d))
            .toList();
        return new TimeFrameSummary(xAxis, values);
    }

    public List<LocalDate> getDatesBetween(LocalDate startDate, LocalDate endDate) {
        long numOfDaysBetween = ChronoUnit.DAYS.between(startDate, endDate);
        var limit = 5;
        var datesBeforeEndStream = IntStream.iterate(0, i -> (numOfDaysBetween - 1) <= limit ? i + 1 : i + (int) Math.ceil((double) numOfDaysBetween / limit))
            .limit(limit)
            .mapToObj(startDate::plusDays);
        return Stream.concat(datesBeforeEndStream, Stream.of(endDate)).toList();
    }
}
