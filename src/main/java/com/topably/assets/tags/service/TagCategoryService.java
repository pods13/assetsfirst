package com.topably.assets.tags.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.slugify.Slugify;
import com.topably.assets.auth.service.UserService;
import com.topably.assets.portfolios.domain.dto.tag.CreateTagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import com.topably.assets.portfolios.domain.dto.tag.UpdateTagCategoryDto;
import com.topably.assets.tags.domain.Tag;
import com.topably.assets.tags.domain.TagCategory;
import com.topably.assets.tags.mapper.TagCategoryMapper;
import com.topably.assets.tags.repository.TagCategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class TagCategoryService {

    private final TagCategoryRepository tagCategoryRepository;
    private final TagCategoryMapper tagCategoryMapper;
    private final UserService userService;
    private final TagService tagService;
    private final Slugify slugify = Slugify.builder().transliterator(true).build();

    public Collection<TagCategoryDto> getTagCategories(Long userId) {
        return tagCategoryRepository.findAllByUserId(userId).stream()
            .map(tagCategoryMapper::modelToDto)
            .toList();
    }

    public TagCategoryDto findUserTagCategoryByCode(Long userId, String code) {
        return tagCategoryRepository.findTagCategoryByUserIdAndCode(userId, code)
            .map(tagCategoryMapper::modelToDto)
            .orElseThrow();
    }

    public Tag findTagByCategoryCodeAndName(String categoryCode, String name) {
        return findTagCategoryByCode(categoryCode).getTags().stream()
            .filter(t -> t.getName().equals(name))
            .findFirst()
            .orElseThrow();
    }

    private TagCategory findTagCategoryByCode(String code) {
        return tagCategoryRepository.findTagCategoryByCode(code)
            .orElseThrow();
    }

    public TagCategoryDto createTagCategory(Long userId, CreateTagCategoryDto dto) {
        var tagCategory = tagCategoryRepository.save(new TagCategory()
            .setName(dto.getName())
            .setCode(slugify.slugify(dto.getName()))
            .setColor(dto.getColor())
            .setUser(userService.getById(userId)));
        var tags = tagService.createTags(tagCategory.getId(), dto.getTags().stream().map(TagDto::getName).toList());
        tagCategory.setTags(new HashSet<>(tags));
        return tagCategoryMapper.modelToDto(tagCategory);
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
        tagCategory.setName(dto.getName());
        tagCategory.setCode(slugify.slugify(dto.getName()));
        return tagCategoryMapper.modelToDto(tagCategory);
    }

    public void deleteTagCategory(Long categoryId) {
        tagCategoryRepository.deleteById(categoryId);
    }

}
