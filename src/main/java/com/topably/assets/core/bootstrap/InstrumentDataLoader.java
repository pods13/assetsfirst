package com.topably.assets.core.bootstrap;

import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.companies.repository.IndustryRepository;
import com.topably.assets.exchanges.domain.Exchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.instruments.domain.instrument.ETF;
import com.topably.assets.instruments.domain.instrument.FX;
import com.topably.assets.instruments.domain.instrument.Stock;
import com.topably.assets.instruments.repository.instrument.ETFRepository;
import com.topably.assets.instruments.repository.instrument.FXRepository;
import com.topably.assets.instruments.repository.instrument.StockRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        addEtfInstruments();
        addStockInstruments();
        addFXInstruments();
    }

    private void addEtfInstruments() {
        Exchange nysearca = exchangeRepository.findByCode(NYSEARCA.name());

        ETF krbn = etfRepository.save(ETF.builder()
                .attribute(ETF.NAME_ATTRIBUTE, "KraneShares Global Carbon Strategy ETF")
                .exchange(nysearca)
                .ticker("KRBN")
                .build());
    }

    private void addStockInstruments() {
        Company newmontCorp = companyRepository.save(Company.builder()
                .name("Newmont Corporation")
                .subIndustry(industryRepository.findByParent_NameAndName("Metals & Mining", "Gold"))
                .build());
        Exchange nyse = exchangeRepository.findByCode(NYSE.name());
        Exchange xetra = exchangeRepository.findByCode("XETRA");
        Exchange mcx = exchangeRepository.findByCode("MCX");

        Stock newmontStock = stockRepository.save(Stock.builder()
                .company(newmontCorp)
                .ticker("NEM")
                .exchange(nyse)
                .build());

        Company bayer = companyRepository.save(Company.builder()
                .name("Bayer AG")
                .subIndustry(industryRepository.findByParent_NameAndName("Pharmaceuticals", "Pharmaceuticals"))
                .build());
        Stock bayerStock = stockRepository.save(Stock.builder()
                .company(bayer)
                .exchange(xetra)
                .ticker("BAYN")
                .build());

        Company altria = companyRepository.save(Company.builder()
                .name("Altria Group")
                .build());
        Stock moStock = stockRepository.save(Stock.builder()
                .company(altria)
                .exchange(nyse)
                .ticker("MO")
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
                .subIndustry(industryRepository.findByParent_NameAndName("Oil, Gas & Consumable Fuels", "Integrated Oil & Gas"))
                .build());
        Stock totalStock = stockRepository.save(Stock.builder()
                .company(total)
                .exchange(nyse)
                .ticker("TTE")
                .build());

        Company rosneft = companyRepository.save(Company.builder()
                .name("PJSC Rosneft Oil Company")
                .subIndustry(industryRepository.findByParent_NameAndName("Oil, Gas & Consumable Fuels", "Integrated Oil & Gas"))
                .build());
        Stock rosnStock = stockRepository.save(Stock.builder()
                .company(rosneft)
                .exchange(mcx)
                .ticker("ROSN")
                .build());

        Company gazprom = companyRepository.save(Company.builder()
                .name("PJSC Gazprom")
                .subIndustry(industryRepository.findByParent_NameAndName("Oil, Gas & Consumable Fuels", "Integrated Oil & Gas"))
                .build());
        Stock gazpromStock = stockRepository.save(Stock.builder()
                .company(gazprom)
                .exchange(mcx)
                .ticker("GAZP")
                .build());

        Company rosAgro = companyRepository.save(Company.builder()
                .name("Ros Agro PLC")
                .subIndustry(industryRepository.findByParent_NameAndName("Food Products", "Agricultural Products"))
                .build());
        Stock rosAgroStock = stockRepository.save(Stock.builder()
                .company(rosAgro)
                .exchange(mcx)
                .ticker("AGRO")
                .build());

        Company polyus = companyRepository.save(Company.builder()
                .name("PJSC Polyus")
                .subIndustry(industryRepository.findByParent_NameAndName("Metals & Mining", "Gold"))
                .build());
        Stock polyusStock = stockRepository.save(Stock.builder()
                .company(polyus)
                .exchange(mcx)
                .ticker("PLZL")
                .build());

        Company phos = companyRepository.save(Company.builder()
                .name("PhosAgro")
                .subIndustry(industryRepository.findByParent_NameAndName("Chemicals", "Fertilizers & Agricultural Chemicals"))
                .build());
        Stock phosStock = stockRepository.save(Stock.builder()
                .company(phos)
                .exchange(mcx)
                .ticker("PHOR")
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
        var gldrub_tom = FX.builder()
                .exchange(mcx)
                .ticker("GLDRUB_TOM")
                .build();
        fxRepository.saveAll(List.of(usdrub_tom, eurrub_tom, gldrub_tom));
    }
}
