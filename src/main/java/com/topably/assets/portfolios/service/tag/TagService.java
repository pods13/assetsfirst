package com.topably.assets.portfolios.service.tag;

import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import com.topably.assets.portfolios.domain.tag.Tag;
import com.topably.assets.portfolios.domain.tag.TagCategory;
import com.topably.assets.portfolios.mapper.TagMapper;
import com.topably.assets.portfolios.repository.tag.TagCategoryRepository;
import com.topably.assets.portfolios.repository.tag.TagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class TagService {

    private final TagCategoryRepository tagCategoryRepository;
    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    public List<TagDto> addTags(Long categoryId, List<String> tags) {
        var tagsToSave = tags.stream()
            .map(t -> new Tag()
                .setName(t)
                .setCategory(tagCategoryRepository.getReferenceById(categoryId)))
            .toList();
        return tagRepository.saveAll(tagsToSave).stream()
            .map(tagMapper::modelToDto)
            .toList();
    }

    public Collection<TagDto> getTagsByCategory(Long categoryId) {
        return tagRepository.findAllByCategoryId(categoryId).stream()
            .map(tagMapper::modelToDto)
            .toList();
    }

    public TagDto addTagToCategory(Long categoryId, String tagName) {
        var tagCategory = tagCategoryRepository.getReferenceById(categoryId);
        if (!StringUtils.hasText(tagName)) {
            throw new RuntimeException("Wrong tag name was provided");
        }
        var tag = new Tag().setCategory(tagCategory).setName(tagName);
        var savedTag = tagRepository.save(tag);
        return tagMapper.modelToDto(savedTag);
    }

    public void deleteCategoryTag(Long tagId) {
        tagRepository.deleteById(tagId);
    }
}
