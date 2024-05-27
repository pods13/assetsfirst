package com.topably.assets.tags.service;

import java.util.stream.Stream;

import com.topably.assets.portfolios.domain.dto.tag.CreateTagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class TagCategoryTrialDataProvider {

    private final TagCategoryService tagCategoryService;

    public void provideData(Long userId) {
        var riskTags = Stream.of("Currency", "Sectoral", "Political", "Sanctions")
            .map(name -> new TagDto().setName(name)).toList();
        tagCategoryService.createTagCategory(userId, new CreateTagCategoryDto()
            .setName("Risk")
            .setColor("#91cc75")
            .setTags(riskTags));

        var regionTags = Stream.of("Russia", "USA", "Europe", "Earth")
            .map(name -> new TagDto().setName(name)).toList();
        tagCategoryService.createTagCategory(userId, new CreateTagCategoryDto()
            .setName("Region")
            .setColor("#5470c6")
            .setTags(regionTags));

        var brokerTags = Stream.of("Interactive Brokers LLC",
                "Tinkoff Investments",
                "VTB Investments",
                "Finam",
                "Alfa Direct",
                "BCS Investments")
            .map(name -> new TagDto().setName(name)).toList();
        tagCategoryService.createTagCategory(userId, new CreateTagCategoryDto()
            .setName("Broker")
            .setColor("#252aad")
            .setTags(brokerTags));
    }

}
