package com.topably.assets.core.bootstrap;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import com.topably.assets.companies.domain.dto.CompanyDataDto;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.findata.exchanges.domain.ExchangeEnum;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.domain.instrument.ETF;
import com.topably.assets.instruments.domain.instrument.FX;
import com.topably.assets.instruments.repository.instrument.ETFRepository;
import com.topably.assets.instruments.repository.instrument.FXRepository;
import com.topably.assets.instruments.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.topably.assets.findata.exchanges.domain.ExchangeEnum.NYSE;
import static com.topably.assets.findata.exchanges.domain.ExchangeEnum.NYSEARCA;


@RequiredArgsConstructor
@Component
@Order(25)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class InstrumentDataLoader implements CommandLineRunner {

    private final ETFRepository etfRepository;
    private final FXRepository fxRepository;

    private final StockService stockService;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        addEtfInstruments();
        addStockInstruments();
        addFXInstruments();
    }

    private void addEtfInstruments() {
        var etfs = new ArrayList<ETF>();

        etfs.add(ETF.builder()
            .name("KraneShares Global Carbon Strategy ETF")
            .exchangeCode(NYSEARCA.name())
            .symbol("KRBN")
            .currency(Currency.getInstance("USD"))
            .build());

        etfs.add(ETF.builder()
            .name("FinEx China UCITS ETF")
            .exchangeCode(ExchangeEnum.MCX.name())
            .symbol("FXCN")
            .currency(Currency.getInstance("RUB"))
            .build());

        etfRepository.saveAll(etfs);
    }

    private void addStockInstruments() {
        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Newmont Goldcorp Corp").build())
            .identifier(new Ticker("NEM", NYSE.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Bayer AG NA").build())
            .identifier(new Ticker("BAYN", ExchangeEnum.XETRA.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Altria Group").build())
            .identifier(new Ticker("MO", NYSE.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Omega Healthcare Investors, Inc").build())
            .identifier(new Ticker("OHI", NYSE.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("The Coca-Cola Company").build())
            .identifier(new Ticker("KO", NYSE.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("TotalEnergies SE").build())
            .identifier(new Ticker("TTE", NYSE.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("PJSC Rosneft Oil Company").build())
            .identifier(new Ticker("ROSN", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("PJSC Gazprom").build())
            .identifier(new Ticker("GAZP", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Ros Agro PLC").build())
            .identifier(new Ticker("AGRO", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("PJSC Polyus").build())
            .identifier(new Ticker("PLZL", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("PhosAgro").build())
            .identifier(new Ticker("PHOR", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Public Joint Stock Company Inter RAO UES").build())
            .identifier(new Ticker("IRAO", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Mobile TeleSystems Public Joint Stock Company").build())
            .identifier(new Ticker("MTSS", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Public Joint Stock Company Magnit").build())
            .identifier(new Ticker("MGNT", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Public Joint-Stock Company Moscow Exchange MICEX-RTS").build())
            .identifier(new Ticker("MOEX", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Sberbank of Russia").build())
            .identifier(new Ticker("SBERP", ExchangeEnum.MCX.name()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("China National Offshore Oil Corporatio").build())
            .identifier(new Ticker("0883", ExchangeEnum.HK.name()))
            .build());
    }

    private void addFXInstruments() {
        var usdrub = FX.builder()
            .name("Currency USD.RUB")
            .exchangeCode(ExchangeEnum.MCX.name())
            .symbol("USD.RUB")
            .currency(Currency.getInstance("USD"))
            .build();
        var eurrub = FX.builder()
            .exchangeCode(ExchangeEnum.FX_IDC.name())
            .name("Currency EUR.RUB")
            .symbol("EUR.RUB")
            .currency(Currency.getInstance("EUR"))
            .build();
        var cnyrub = FX.builder()
            .exchangeCode(ExchangeEnum.FX_IDC.name())
            .name("Currency CNY.RUB")
            .symbol("CNY.RUB")
            .currency(Currency.getInstance("CNY"))
            .build();
        var hkdrub = FX.builder()
            .exchangeCode(ExchangeEnum.FX_IDC.name())
            .name("Currency HKD.RUB")
            .symbol("HKD.RUB")
            .currency(Currency.getInstance("HKD"))
            .build();
        var gldrub_tom = FX.builder()
            .exchangeCode(ExchangeEnum.MCX.name())
            .name("Золото")
            .symbol("GLDRUB_TOM")
            .currency(Currency.getInstance("RUB"))
            .build();
        var rubUsd = FX.builder()
            .exchangeCode(ExchangeEnum.FX_IDC.name())
            .name("Валюта RUB.USD")
            .symbol("RUB.USD")
            .currency(Currency.getInstance("RUB"))
            .build();
        fxRepository.saveAll(List.of(usdrub, eurrub, cnyrub, hkdrub, gldrub_tom, rubUsd));
    }

}
