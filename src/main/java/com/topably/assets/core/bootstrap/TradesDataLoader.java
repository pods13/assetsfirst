package com.topably.assets.core.bootstrap;

import com.topably.assets.auth.domain.User;
import com.topably.assets.auth.repository.UserRepository;
import com.topably.assets.companies.domain.Company;
import com.topably.assets.companies.repository.CompanyRepository;
import com.topably.assets.exchanges.domain.Exchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.securities.domain.ETF;
import com.topably.assets.securities.domain.Stock;
import com.topably.assets.securities.repository.security.ETFRepository;
import com.topably.assets.securities.repository.security.StockRepository;
import com.topably.assets.trades.domain.TradeOperation;
import com.topably.assets.trades.domain.security.SecurityTrade;
import com.topably.assets.trades.repository.SecurityTradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static com.topably.assets.exchanges.domain.USExchange.NYSE;
import static com.topably.assets.exchanges.domain.USExchange.NYSEARCA;

@RequiredArgsConstructor
@Component
@Order(3)
@ConditionalOnProperty(name = "app.bootstrap.with.data", havingValue = "true")
public class TradesDataLoader implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final ExchangeRepository exchangeRepository;

    private final StockRepository stockRepository;
    private final ETFRepository exchangeTradedFundRepository;
    private final SecurityTradeRepository securityTradeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        User user = userRepository.getById(1L);
        addNewmont(user);
        addKRBN(user);

        addBayer(user);
        addRosneft(user);

        addAltria(user);
        addGazprom(user);
        addRosAgro(user);
        addPolyus(user);
    }

    private void addNewmont(User user) {
        Company newmontCorp = companyRepository.save(Company.builder()
                .name("Newmont Corporation")
                .build());
        Exchange nyse = exchangeRepository.findByCode(NYSE.name());

        Stock newmontStock = stockRepository.save(Stock.builder()
                .company(newmontCorp)
                .ticker("NEM")
                .exchange(nyse)
                .build());
        securityTradeRepository.save(SecurityTrade.builder()
                .date(LocalDateTime.now(ZoneOffset.UTC))
                .security(newmontStock)
                .user(user)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(67.99))
                .quantity(BigInteger.valueOf(15L))
                .build());
    }

    private void addKRBN(User user) {
        Exchange nysearca = exchangeRepository.findByCode(NYSEARCA.name());

        ETF krbn = exchangeTradedFundRepository.save(ETF.builder()
                .name("KraneShares Global Carbon Strategy ETF")
                .exchange(nysearca)
                .ticker("KRBN")
                .build());
        securityTradeRepository.save(SecurityTrade.builder()
                .security(krbn)
                .date(LocalDateTime.now(ZoneOffset.UTC))
                .user(user)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(40.67))
                .quantity(BigInteger.valueOf(24L))
                .build());
    }

    private void addBayer(User user) {
        Company bayer = companyRepository.save(Company.builder()
                .name("Bayer AG")
                .build());

        Exchange xetra = exchangeRepository.findByCode("XETRA");
        Stock bayerStock = stockRepository.save(Stock.builder()
                .company(bayer)
                .exchange(xetra)
                .ticker("BAYN")
                .build());
        securityTradeRepository.save(SecurityTrade.builder()
                .date(LocalDateTime.now(ZoneOffset.UTC))
                .security(bayerStock)
                .user(user)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(47.23))
                .quantity(BigInteger.valueOf(110L))
                .build());
    }

    private void addAltria(User user) {
        Company altria = companyRepository.save(Company.builder()
                .name("Altria Group")
                .build());

        Exchange nyse = exchangeRepository.findByCode(NYSE.name());
        Stock moStock = stockRepository.save(Stock.builder()
                .company(altria)
                .exchange(nyse)
                .ticker("MO")
                .build());

        securityTradeRepository.save(SecurityTrade.builder()
                .date(LocalDateTime.of(2021, 12, 1, 11, 0))
                .security(moStock)
                .user(user)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(45.67))
                .quantity(BigInteger.valueOf(64L))
                .build());
    }

    private void addRosneft(User user) {
        Company rosneft = companyRepository.save(Company.builder()
                .name("PJSC Rosneft Oil Company")
                .build());

        Exchange mcx = exchangeRepository.findByCode("MCX");

        Stock rosnStock = stockRepository.save(Stock.builder()
                .company(rosneft)
                .exchange(mcx)
                .ticker("ROSN")
                .build());

        securityTradeRepository.save(SecurityTrade.builder()
                .date(LocalDateTime.of(2022, 1, 18, 8, 0))
                .security(rosnStock)
                .user(user)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(393.15))
                .quantity(BigInteger.valueOf(700L))
                .build());

        securityTradeRepository.save(SecurityTrade.builder()
                .date(LocalDateTime.of(2022, 3, 24, 8, 0))
                .security(rosnStock)
                .user(user)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(357.12))
                .quantity(BigInteger.valueOf(250L))
                .build());
    }

    private void addGazprom(User user) {
        Company gazprom = companyRepository.save(Company.builder()
                .name("PJSC Gazprom")
                .build());

        Exchange mcx = exchangeRepository.findByCode("MCX");
        Stock gazpromStock = stockRepository.save(Stock.builder()
                .company(gazprom)
                .exchange(mcx)
                .ticker("GAZP")
                .build());

        securityTradeRepository.save(SecurityTrade.builder()
                .date(LocalDateTime.now(ZoneOffset.UTC))
                .security(gazpromStock)
                .user(user)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(227))
                .quantity(BigInteger.valueOf(900L))
                .build());
    }

    private void addRosAgro(User user) {
        Company rosAgro = companyRepository.save(Company.builder()
                .name("Ros Agro PLC")
                .build());

        Exchange mcx = exchangeRepository.findByCode("MCX");
        Stock rosAgroStock = stockRepository.save(Stock.builder()
                .company(rosAgro)
                .exchange(mcx)
                .ticker("AGRO")
                .build());

        securityTradeRepository.save(SecurityTrade.builder()
                .date(LocalDateTime.now(ZoneOffset.UTC))
                .security(rosAgroStock)
                .user(user)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(1019))
                .quantity(BigInteger.valueOf(460L))
                .build());
    }

    private void addPolyus(User user) {
        Company polyus = companyRepository.save(Company.builder()
                .name("PJSC Polyus")
                .build());

        Exchange mcx = exchangeRepository.findByCode("MCX");
        Stock polyusStock = stockRepository.save(Stock.builder()
                .company(polyus)
                .exchange(mcx)
                .ticker("PLZL")
                .build());

        securityTradeRepository.save(SecurityTrade.builder()
                .date(LocalDateTime.now(ZoneOffset.UTC))
                .security(polyusStock)
                .user(user)
                .operation(TradeOperation.BUY)
                .price(BigDecimal.valueOf(13000))
                .quantity(BigInteger.valueOf(27L))
                .build());
    }
}
