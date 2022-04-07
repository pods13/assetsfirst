package com.topably.assets.securities.service;

import com.topably.assets.securities.domain.Security;
import com.topably.assets.securities.domain.SecurityType;
import com.topably.assets.securities.domain.dto.SecurityDto;

import java.util.Collection;

public interface SecurityService {

    Collection<SecurityDto> searchSecurities(String searchTerm, Collection<SecurityType> securityTypes);

    Collection<Security> findCertainTypeOfSecuritiesByExchangeCodes(Collection<SecurityType> securityTypes,
                                                                    Collection<String> exchangeCodes);

    Security findSecurity(String ticker, String exchange);
}
