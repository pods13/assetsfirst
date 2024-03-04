package com.topably.assets.trades.service.broker;

import java.util.Collection;

import com.topably.assets.portfolios.domain.dto.tag.TagCategoryDto;
import com.topably.assets.portfolios.service.tag.TagCategoryService;
import com.topably.assets.trades.domain.dto.broker.BrokerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BrokerService {

    private static final String BROKER_TAG_CATEGORY = "Broker";
    private final TagCategoryService tagCategoryService;

    public Collection<BrokerDto> getBrokers(Long userId) {
        return tagCategoryService.findTagCategoryByName(userId, BROKER_TAG_CATEGORY).stream()
            .map(TagCategoryDto::getTags)
            .flatMap(Collection::stream)
            .map(tag -> new BrokerDto(tag.getId(), tag.getName()))
            .toList();
    }

}
