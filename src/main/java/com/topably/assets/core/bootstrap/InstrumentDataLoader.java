package com.topably.assets.core.bootstrap;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.domain.dto.CompanyDataDto;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.companies.repository.IndustryRepository;
import com.topably.assets.core.domain.Ticker;
import com.topably.assets.exchanges.domain.Exchange;
import com.topably.assets.exchanges.domain.ExchangeEnum;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.instruments.domain.dto.StockDataDto;
import com.topably.assets.instruments.domain.instrument.ETF;
import com.topably.assets.instruments.domain.instrument.FX;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.repository.instrument.ETFRepository;
import com.topably.assets.instruments.repository.instrument.FXRepository;
import com.topably.assets.instruments.repository.instrument.StockRepository;
import com.topably.assets.instruments.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Currency;
import java.util.List;

import static com.topably.assets.exchanges.domain.USExchange.NYSE;
import static com.topably.assets.exchanges.domain.USExchange.NYSEARCA;

@RequiredArgsConstructor
@Component
@Order(25)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class InstrumentDataLoader implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final ExchangeRepository exchangeRepository;
    private final IndustryRepository industryRepository;

    private final StockRepository stockRepository;
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
        Exchange nysearca = exchangeRepository.findByCode(NYSEARCA.name());
        Exchange mcx = exchangeRepository.findByCode(ExchangeEnum.MCX.name());

        var etfs = new ArrayList<ETF>();

        etfs.add(ETF.builder()
            .attribute(ETF.NAME_ATTRIBUTE, "KraneShares Global Carbon Strategy ETF")
            .exchange(nysearca)
            .ticker("KRBN")
            .currency(mcx.getCurrency())
            .build());

        etfs.add(ETF.builder()
            .attribute(ETF.NAME_ATTRIBUTE, "FinEx China UCITS ETF")
            .exchange(mcx)
            .ticker("FXCN")
            .currency(mcx.getCurrency())
            .build());

        etfRepository.saveAll(etfs);
    }

    private void addStockInstruments() {
        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Newmont Goldcorp Corp").build())
            .identifier(new Ticker("NEM", NYSE.name()))
            .build());

        Exchange mcx = exchangeRepository.findByCode(ExchangeEnum.MCX.name());
        Exchange hkex = exchangeRepository.findByCode("HK");

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
            .identifier(new Ticker("ROSN", mcx.getCode()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("PJSC Gazprom").build())
            .identifier(new Ticker("GAZP", mcx.getCode()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Ros Agro PLC").build())
            .identifier(new Ticker("AGRO", mcx.getCode()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("PJSC Polyus").build())
            .identifier(new Ticker("PLZL", mcx.getCode()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("PhosAgro").build())
            .identifier(new Ticker("PHOR", mcx.getCode()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Public Joint Stock Company Inter RAO UES").build())
            .identifier(new Ticker("IRAO", mcx.getCode()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Mobile TeleSystems Public Joint Stock Company").build())
            .identifier(new Ticker("MTSS", mcx.getCode()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Public Joint Stock Company Magnit").build())
            .identifier(new Ticker("MGNT", mcx.getCode()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("Public Joint-Stock Company Moscow Exchange MICEX-RTS").build())
            .identifier(new Ticker("MOEX", mcx.getCode()))
            .build());

        stockService.addStock(StockDataDto.builder()
            .company(CompanyDataDto.builder().name("China National Offshore Oil Corporatio").build())
            .identifier(new Ticker("0883", hkex.getCode()))
            .build());
    }

    private void addFXInstruments() {
        Exchange mcx = exchangeRepository.findByCode(ExchangeEnum.MCX.name());
        Exchange fxIDC = exchangeRepository.findByCode(ExchangeEnum.FX_IDC.name());
        var usdrub = FX.builder()
            .exchange(mcx)
            .ticker("USD.RUB")
            .currency(Currency.getInstance("USD"))
            .build();
        var eurrub = FX.builder()
            .exchange(fxIDC)
            .ticker("EUR.RUB")
            .currency(Currency.getInstance("EUR"))
            .build();
        var cnyrub = FX.builder()
            .exchange(fxIDC)
            .ticker("CNY.RUB")
            .currency(Currency.getInstance("CNY"))
            .build();
        var hkdrub = FX.builder()
            .exchange(fxIDC)
            .ticker("HKD.RUB")
            .currency(Currency.getInstance("HKD"))
            .build();
        var gldrub_tom = FX.builder()
            .exchange(mcx)
            .ticker("GLDRUB_TOM")
            .currency(mcx.getCurrency())
            .build();
        var rubUsd = FX.builder()
            .exchange(fxIDC)
            .ticker("RUB.USD")
            .currency(Currency.getInstance("RUB"))
            .build();
        fxRepository.saveAll(List.of(usdrub, eurrub, cnyrub, hkdrub, gldrub_tom, rubUsd));
    }
}
