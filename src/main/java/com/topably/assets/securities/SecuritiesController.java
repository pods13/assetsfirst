package com.topably.assets.securities;

import com.topably.assets.securities.domain.SecurityType;
import com.topably.assets.securities.domain.dto.SecurityDto;
import com.topably.assets.securities.service.SecurityService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;
import java.util.Set;

@RestController
@RequestMapping("/securities")
@RequiredArgsConstructor
public class SecuritiesController {

    private final SecurityService securityService;

    @GetMapping("")
    public Collection<SecurityDto> getSecurities(@RequestParam(value = "search", required = false) String search,
                                                 @RequestParam(required = false) Collection<SecurityType> securityTypes) {
        var types = CollectionUtils.isEmpty(securityTypes) ? Set.of(SecurityType.values()) : securityTypes;
        if (StringUtils.hasText(search)) {
            return securityService.searchSecurities(search, types);
        }

        return securityService.searchSecurities("", types);
    }
}
