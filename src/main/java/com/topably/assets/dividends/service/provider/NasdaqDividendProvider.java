package com.topably.assets.dividends.service.provider;

import com.topably.assets.dividends.domain.dto.DividendData;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class NasdaqDividendProvider implements DividendProvider {

    private static final String COMMON_URL = "https://www.nasdaq.com/market-activity/stocks/%s/dividend-history";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @Override
    public Collection<DividendData> getDividendHistory(String ticker) {
        var tbodyContainer = getDividendHistoryContainer(ticker);
        if (tbodyContainer == null) {
            return Collections.emptyList();
        }
        return tbodyContainer.getElementsByTag("tr").stream()
                .map(this::convertToDivData)
                .filter(Objects::isNull)
                .collect(Collectors.toList());
    }

    private Element getDividendHistoryContainer(String ticker) {
        var tickerUrl = String.format(COMMON_URL, ticker);
        try {
            Document doc = Jsoup.connect(tickerUrl).get();
            return doc.getElementsByClass("dividend-history__table-body").get(0);
        } catch (Exception e) {
            log.error("Cannot locate dividend history container for " + ticker, e);
        }
        return null;
    }

    private DividendData convertToDivData(Element row) {
        Elements dataElements = row.getElementsByTag("td");
        if (dataElements.isEmpty() || dataElements.size() < 5) {
            return null;
        }
        return DividendData.builder()
                .amount(parseAmount(dataElements.get(1)))
                .declareDate(parseDate(dataElements.get(2)))
                .recordDate(parseDate(dataElements.get(3)))
                .payDate(parseDate(dataElements.get(4)))
                .build();
    }

    private BigDecimal parseAmount(Element dataElement) {
        var number = dataElement.data().replaceAll("[^\\d.]", "");
        return new BigDecimal(number);
    }

    private LocalDate parseDate(Element dataElement) {
        if (dataElement == null || !StringUtils.hasText(dataElement.data())) {
            return null;
        }
        return LocalDate.parse(dataElement.data(), DATE_TIME_FORMATTER);
    }
}
