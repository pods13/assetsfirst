package com.topably.assets.portfolios.service.tag;

import com.topably.assets.portfolios.domain.dto.tag.CreateTagCategoryDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagCategoryTrialDataProvider {

    private final TagCategoryService tagCategoryService;

    public void provideData(Long userId) {
        var riskTags = List.of("Currency", "Sectoral", "Political", "Sanctions");
        tagCategoryService.createTagCategory(userId, new CreateTagCategoryDto()
            .setName("Risk")
            .setColor("#91cc75")
            .setTags(riskTags));

        var regionTags = List.of("Russia", "USA", "Europe", "Earth");
        tagCategoryService.createTagCategory(userId, new CreateTagCategoryDto()
            .setName("Region")
            .setColor("#5470c6")
            .setTags(regionTags));
    }
}
