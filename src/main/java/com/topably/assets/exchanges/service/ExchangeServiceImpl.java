package com.topably.assets.exchanges.service;

import com.topably.assets.exchanges.domain.TickerSymbol;
import com.topably.assets.exchanges.domain.USExchange;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.securities.domain.SecurityType;
import com.topably.assets.securities.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private static final Set<String> US_EXCHANGE_CODES = Arrays.stream(USExchange.values()).map(USExchange::name).collect(toSet());

    private final ExchangeRepository exchangeRepository;
    private final SecurityService securityService;

    @Override
    @Transactional
    public Collection<TickerSymbol> findTickersByExchange(String exchange) {
        var exchangeCodes = "US".equals(exchange) ? US_EXCHANGE_CODES : Set.of(exchange);
        var securityTypes = Set.of(SecurityType.STOCK, SecurityType.ETF);
        var securities = securityService.findCertainTypeOfSecuritiesByExchangeCodes(securityTypes, exchangeCodes);
        return securities.stream()
                .map(security -> new TickerSymbol(security.getTicker(), security.getExchange().getCode()))
                .collect(toList());
    }
}
