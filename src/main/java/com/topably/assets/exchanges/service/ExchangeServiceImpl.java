package com.topably.assets.exchanges.service;

import com.topably.assets.exchanges.domain.TickerDto;
import com.topably.assets.exchanges.repository.ExchangeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@RequiredArgsConstructor
public class ExchangeServiceImpl implements ExchangeService {

    private final ExchangeRepository exchangeRepository;

    @Override
    @Transactional
    public Collection<TickerDto> findTickersByExchange(String exchange) {
        return null;
    }
}
