package com.topably.assets.securities.service;

import com.topably.assets.securities.domain.SecurityType;
import com.topably.assets.securities.domain.dto.SecurityDto;
import com.topably.assets.securities.repository.SecurityRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SecurityService {

    private final SecurityRepository securityRepository;

    public List<SecurityDto> searchSecurities(String searchTerm, Collection<SecurityType> securityTypes) {
        return securityRepository.searchSecurities(searchTerm, securityTypes);
    }
}
