package com.topably.assets.portfolios.service.tag;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.domain.dto.tag.CreateTagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import com.topably.assets.portfolios.domain.dto.tag.UpdateTagCategoryDto;
import com.topably.assets.portfolios.domain.tag.Tag;
import com.topably.assets.portfolios.domain.tag.TagCategory;
import com.topably.assets.portfolios.mapper.TagCategoryMapper;
import com.topably.assets.portfolios.repository.tag.TagCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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
            .map(c -> tagCategoryMapper.modelToDto(c, c.getTags()))
            .toList();
    }

    public TagCategoryDto createTagCategory(Long userId, CreateTagCategoryDto dto) {
        var tagCategory = tagCategoryRepository.save(new TagCategory()
            .setName(dto.getName())
            .setColor(dto.getColor())
            .setUser(userService.getById(userId)));
        var tags = tagService.createTags(tagCategory.getId(), dto.getTags().stream().map(TagDto::getName).toList());
        return tagCategoryMapper.modelToDto(tagCategory, tags);
    }

    public TagCategoryDto updateTagCategory(Long categoryId, UpdateTagCategoryDto dto) {
        var tagCategory = tagCategoryRepository.findById(categoryId).orElseThrow();
        var partitionedTags = dto.getTags().stream().collect(Collectors.partitioningBy(t -> Objects.isNull(t.getId())));
        var newTagNames = partitionedTags.get(Boolean.TRUE).stream().map(TagDto::getName).toList();
        var newTags = tagService.createTags(categoryId, newTagNames);

        var remainedTagIds = partitionedTags.get(Boolean.FALSE).stream().map(TagDto::getId).collect(Collectors.toSet());
        var remainedTags = tagCategory.getTags().stream().filter(t -> remainedTagIds.contains(t.getId())).toList();
        tagCategory.getTags().clear();
        tagCategory.getTags().addAll(Stream.concat(remainedTags.stream(), newTags.stream()).toList());
        return tagCategoryMapper.modelToDto(tagCategory, tagCategory.getTags());
    }

    public void deleteTagCategory(Long categoryId) {
        tagCategoryRepository.deleteById(categoryId);
    }
}
