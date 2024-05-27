package com.topably.assets.trades.service.broker;

import java.util.Collection;

import com.topably.assets.tags.service.TagCategoryService;
import com.topably.assets.trades.domain.dto.broker.BrokerDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class BrokerService {

    private static final String BROKER_TAG_CATEGORY = "broker";
    private final TagCategoryService tagCategoryService;

    public Collection<BrokerDto> getBrokers(Long userId) {
        return tagCategoryService.findUserTagCategoryByCode(userId, BROKER_TAG_CATEGORY).getTags().stream()
            .map(tag -> new BrokerDto(tag.getId(), tag.getName()))
            .toList();
    }

}
