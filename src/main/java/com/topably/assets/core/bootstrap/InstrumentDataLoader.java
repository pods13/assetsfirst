package com.topably.assets.core.bootstrap;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.domain.dto.CompanyDataDto;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.companies.repository.IndustryRepository;
import com.topably.assets.core.domain.TickerSymbol;
import com.topably.assets.exchanges.domain.Exchange;
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
        Exchange mcx = exchangeRepository.findByCode("MCX");

        var etfs = new ArrayList<ETF>();

        etfs.add(ETF.builder()
                .attribute(ETF.NAME_ATTRIBUTE, "KraneShares Global Carbon Strategy ETF")
                .exchange(nysearca)
                .ticker("KRBN")
                .build());

        etfs.add(ETF.builder()
                .attribute(ETF.NAME_ATTRIBUTE, "FinEx China UCITS ETF")
                .exchange(mcx)
                .ticker("FXCN")
                .build());

        etfRepository.saveAll(etfs);
    }

    private void addStockInstruments() {
        stockService.addStock(StockDataDto.builder()
                .company(CompanyDataDto.builder().name("Newmont Goldcorp Corp").build())
                .identifier(new TickerSymbol("NEM", NYSE.name()))
                .build());

        Exchange nyse = exchangeRepository.findByCode(NYSE.name());
        Exchange xetra = exchangeRepository.findByCode("XETRA");
        Exchange mcx = exchangeRepository.findByCode("MCX");
        Exchange hkex = exchangeRepository.findByCode("HK");

        stockService.addStock(StockDataDto.builder()
                .company(CompanyDataDto.builder().name("Bayer AG NA").build())
                .identifier(new TickerSymbol("BAYGn", "XETRA"))
                .build());

        stockService.addStock(StockDataDto.builder()
                .company(CompanyDataDto.builder().name("Altria Group").build())
                .identifier(new TickerSymbol("MO", NYSE.name()))
                .build());

        Company omega = companyRepository.save(Company.builder()
                .name("Omega Healthcare Investors, Inc")
                .build());
        Stock ohiStock = stockRepository.save(Stock.builder()
                .company(omega)
                .exchange(nyse)
                .ticker("OHI")
                .build());

        Company coke = companyRepository.save(Company.builder()
                .name("The Coca-Cola Company")
                .build());
        Stock koStock = stockRepository.save(Stock.builder()
                .company(coke)
                .exchange(nyse)
                .ticker("KO")
                .build());

        Company total = companyRepository.save(Company.builder()
                .name("TotalEnergies SE")
                .build());
        Stock totalStock = stockRepository.save(Stock.builder()
                .company(total)
                .exchange(nyse)
                .ticker("TTE")
                .build());

        Company rosneft = companyRepository.save(Company.builder()
                .name("PJSC Rosneft Oil Company")
                .build());
        Stock rosnStock = stockRepository.save(Stock.builder()
                .company(rosneft)
                .exchange(mcx)
                .ticker("ROSN")
                .build());

        Company gazprom = companyRepository.save(Company.builder()
                .name("PJSC Gazprom")
                .build());
        Stock gazpromStock = stockRepository.save(Stock.builder()
                .company(gazprom)
                .exchange(mcx)
                .ticker("GAZP")
                .build());

        Company rosAgro = companyRepository.save(Company.builder()
                .name("Ros Agro PLC")
                .build());
        Stock rosAgroStock = stockRepository.save(Stock.builder()
                .company(rosAgro)
                .exchange(mcx)
                .ticker("AGRO")
                .build());

        Company polyus = companyRepository.save(Company.builder()
                .name("PJSC Polyus")
                .build());
        Stock polyusStock = stockRepository.save(Stock.builder()
                .company(polyus)
                .exchange(mcx)
                .ticker("PLZL")
                .build());

        Company phos = companyRepository.save(Company.builder()
                .name("PhosAgro")
                .build());
        Stock phosStock = stockRepository.save(Stock.builder()
                .company(phos)
                .exchange(mcx)
                .ticker("PHOR")
                .build());

        Company irao = companyRepository.save(Company.builder()
                .name("Public Joint Stock Company Inter RAO UES")
                .build());
        Stock iraoStock = stockRepository.save(Stock.builder()
                .company(irao)
                .exchange(mcx)
                .ticker("IRAO")
                .build());

        Company mts = companyRepository.save(Company.builder()
                .name("Mobile TeleSystems Public Joint Stock Company")
                .build());
        Stock mtsStock = stockRepository.save(Stock.builder()
                .company(mts)
                .exchange(mcx)
                .ticker("MTSS")
                .build());

        Company magnit = companyRepository.save(Company.builder()
                .name("Public Joint Stock Company Magnit")
                .build());
        Stock magnitStock = stockRepository.save(Stock.builder()
                .company(magnit)
                .exchange(mcx)
                .ticker("MGNT")
                .build());

        Company moex = companyRepository.save(Company.builder()
                .name("Public Joint-Stock Company Moscow Exchange MICEX-RTS")
                .build());
        Stock moexStock = stockRepository.save(Stock.builder()
                .company(moex)
                .exchange(mcx)
                .ticker("MOEX")
                .build());

        Company cnooc = companyRepository.save(Company.builder()
                .name("China National Offshore Oil Corporation")
                .build());
        Stock cnoocStock = stockRepository.save(Stock.builder()
                .company(cnooc)
                .exchange(hkex)
                .ticker("0883")
                .build());
    }

    private void addFXInstruments() {
        Exchange mcx = exchangeRepository.findByCode("MCX");
        var usdrub_tom = FX.builder()
                .exchange(mcx)
                .ticker("USDRUB_TOM")
                .build();
        var eurrub_tom = FX.builder()
                .exchange(mcx)
                .ticker("EURRUB_TOM")
                .build();
        var cnyrub_tom = FX.builder()
                .exchange(mcx)
                .ticker("CNYRUB_TOM")
                .build();
        var hkdrub_tom = FX.builder()
                .exchange(mcx)
                .ticker("HKDRUB_TOM")
                .build();
        var gldrub_tom = FX.builder()
                .exchange(mcx)
                .ticker("GLDRUB_TOM")
                .build();
        fxRepository.saveAll(List.of(usdrub_tom, eurrub_tom, cnyrub_tom, hkdrub_tom, gldrub_tom));
    }
}
