package com.topably.assets.trades.brokerage.vtb;

import com.topably.assets.trades.domain.dto.TradeImportDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

@Slf4j
public class TradeWriter implements ItemWriter<TradeImportDto> {

    @Override
    public void write(List<? extends TradeImportDto> items) throws Exception {
//        items.forEach(i -> log.info(i.toString()));
    }
}
