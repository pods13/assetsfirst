package com.topably.assets.dividends.service;

import com.topably.assets.dividends.domain.Dividend;
import com.topably.assets.dividends.domain.dto.DividendData;
import com.topably.assets.dividends.repository.DividendRepository;
import com.topably.assets.securities.domain.Security;
import com.topably.assets.securities.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

import static java.util.stream.Collectors.toList;

@Service
@RequiredArgsConstructor
public class DividendServiceImpl implements DividendService {

    private final SecurityService securityService;
    private final DividendRepository dividendRepository;

    @Override
    @Transactional
    public void addDividends(String ticker, String exchange, Collection<DividendData> dividendData) {
        Collection<Dividend> securityDividends = collectDividendsToPersist(ticker, exchange, dividendData);
        dividendRepository.saveAll(securityDividends);
    }

    private Collection<Dividend> collectDividendsToPersist(String ticker, String exchange,
                                                           Collection<DividendData> dividendData) {
        Dividend lastDeclaredDividend = dividendRepository.findLastDeclaredDividend(ticker, exchange);
        if (lastDeclaredDividend == null) {
            Security security = securityService.findSecurity(ticker, exchange);
            return dividendData.stream()
                    .map(data -> convertToDividend(data, security))
                    .collect(toList());
        }
        return dividendData.stream()
                .filter(data -> data.getDeclareDate() != null
                        && data.getDeclareDate().compareTo(lastDeclaredDividend.getDeclareDate()) > 0)
                .map(data -> convertToDividend(data, lastDeclaredDividend.getSecurity()))
                .collect(toList());
    }

    private Dividend convertToDividend(DividendData data, Security security) {
        return Dividend.builder()
                .security(security)
                .amount(data.getAmount())
                .declareDate(data.getDeclareDate())
                .recordDate(data.getRecordDate())
                .payDate(data.getPayDate())
                .build();
    }
}
