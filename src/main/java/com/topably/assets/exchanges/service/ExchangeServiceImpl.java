package com.topably.assets.exchanges.service;

import com.topably.assets.exchanges.domain.TickerDto;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import com.topably.assets.securities.domain.SecurityType;
import com.topably.assets.securities.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRepository exchangeRepository;
    private final SecurityService securityService;

    @Override
    @Transactional
    public Collection<TickerDto> findTickersByExchange(String exchange) {
        var securityTypes = Set.of(SecurityType.STOCK, SecurityType.ETF);
        var securities = securityService.findCertainTypeOfSecuritiesByExchangeCodes(securityTypes, Set.of(exchange));
        return securities.stream()
                .map(security -> new TickerDto(security.getTicker(), exchange))
                .collect(toList());
    }
}
