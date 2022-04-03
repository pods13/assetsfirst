package com.topably.assets.trades.brokerage.vtb;

import com.topably.assets.trades.domain.dto.TradeImportDto;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class TradeFilterProcessor implements ItemProcessor<String[], TradeImportDto> {

    private static final String START_HEADER = "Заключенные в отчетном периоде сделки с ценными бумагами";
    private static final String END_HEADER = "Завершенные в отчетном периоде сделки с ценными бумагами (обязательства прекращены)";
    private static final List<String> START_OF_ROW_TO_SKIP = List.of("Наименование ценной бумаги");

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm:ss");

    private final AtomicBoolean involved = new AtomicBoolean();

    @Override
    public TradeImportDto process(String[] row) throws Exception {
        var secondCell = row[1];
        if (START_HEADER.equals(secondCell)) {
            involved.set(true);
            return null;
        } else if (END_HEADER.equals(secondCell)) {
            involved.set(false);
            return null;
        }

        if (involved.get()) {
            if (START_OF_ROW_TO_SKIP.stream().anyMatch(secondCell::startsWith)) {
                return null;
            }
            String[] tradeIds = secondCell.split(", ");
            BigInteger quantity = new BigInteger(row[9].replaceAll("[^0-9]", ""));
            BigDecimal price = BigDecimal.valueOf(parseDouble(row[16]));
            BigDecimal fee = BigDecimal.valueOf(parseDouble(row[33])).add(BigDecimal.valueOf(parseDouble(row[36])));
            return TradeImportDto.builder()
                    .companyName(tradeIds[0])
                    .isin(tradeIds[2])
                    .operation(row[5])
                    .currency(row[12])
                    .price(price)
                    .fee(fee)
                    .tradeNum(row[52])
                    .quantity(quantity)
                    .date(LocalDateTime.parse(row[3], FORMATTER))
                    .build();
        }
        return null;
    }

    private Double parseDouble(String s) {
        return Double.parseDouble(s.replaceAll(",", "."));
    }
}
