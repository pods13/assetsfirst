package com.topably.assets.securities.service;

import com.topably.assets.securities.domain.ETF;
import com.topably.assets.securities.domain.Security;
import com.topably.assets.securities.domain.SecurityType;
import com.topably.assets.securities.domain.Stock;
import com.topably.assets.securities.domain.dto.SecurityDto;
import com.topably.assets.securities.repository.SecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.*;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final SecurityRepository securityRepository;

    public List<SecurityDto> searchSecurities(String searchTerm, Collection<SecurityType> securityTypes) {
        var types = securityTypes.stream().map(SecurityType::name).collect(toSet());
        Collection<Security> securities = securityRepository.searchSecurityByTickerLikeAndTypeIn(searchTerm, types);
        return securities.stream().map(security -> {
            if (security instanceof Stock) {
                return SecurityDto.builder()
                        .id(security.getId())
                        .ticker(security.getTicker())
                        .name(((Stock) security).getCompany().getName())
                        .securityType(SecurityType.STOCK)
                        .build();
            } else if (security instanceof ETF) {
                return SecurityDto.builder()
                        .id(security.getId())
                        .ticker(security.getTicker())
                        .name(((ETF) security).getName())
                        .securityType(SecurityType.ETF)
                        .build();
            }
            return SecurityDto.builder()
                    .id(security.getId())
                    .ticker(security.getTicker())
                    .build();
        }).collect(toList());
    }
}
