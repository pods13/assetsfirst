package com.topably.assets.portfolios.service.tag;

import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.domain.dto.tag.CreateTagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import com.topably.assets.portfolios.domain.tag.TagCategory;
import com.topably.assets.portfolios.mapper.TagCategoryMapper;
import com.topably.assets.portfolios.repository.tag.TagCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagCategoryService {

    private final TagCategoryRepository tagCategoryRepository;
    private final TagCategoryMapper tagCategoryMapper;
    private final UserService userService;
    private final TagService tagService;

    public Collection<TagCategoryDto> getTagCategories(Long userId) {
        return tagCategoryRepository.findAllByUserId(userId).stream()
            .map(c -> tagCategoryMapper.modelToDto(c, null))
            .toList();
    }

    public TagCategoryDto createTagCategory(Long userId, CreateTagCategoryDto dto) {
        var tagCategory = tagCategoryRepository.save(new TagCategory()
            .setName(dto.getName())
            .setColor(dto.getColor())
            .setUser(userService.getById(userId)));
        var tags = tagService.addTags(tagCategory.getId(), dto.getTags());
        return tagCategoryMapper.modelToDto(tagCategory, tags);
    }
}
