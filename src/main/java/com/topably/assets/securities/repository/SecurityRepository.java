package com.topably.assets.securities.repository;

import com.topably.assets.securities.domain.SecurityType;
import com.topably.assets.securities.domain.dto.SecurityDto;

import java.util.Collection;
import java.util.List;

public interface SecurityRepository {

    List<SecurityDto> searchSecurities(String search, Collection<SecurityType> securityTypes);
}
