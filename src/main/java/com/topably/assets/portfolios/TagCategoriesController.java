package com.topably.assets.portfolios;

import java.util.Collection;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.portfolios.domain.dto.tag.CreateTagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import com.topably.assets.portfolios.domain.dto.tag.TagProjection;
import com.topably.assets.portfolios.domain.dto.tag.UpdateTagCategoryDto;
import com.topably.assets.portfolios.service.tag.TagCategoryService;
import com.topably.assets.portfolios.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/tag-categories")
@RequiredArgsConstructor
public class TagCategoriesController {

    private final TagCategoryService tagCategoryService;
    private final TagService tagService;

    @GetMapping
    public Collection<TagCategoryDto> getTagCategories(@AuthenticationPrincipal CurrentUser user) {
        return tagCategoryService.getTagCategories(user.getUserId());
    }

    @PostMapping
    public TagCategoryDto createTagCategory(
        @AuthenticationPrincipal CurrentUser user,
        @Validated @RequestBody CreateTagCategoryDto dto
    ) {
        return tagCategoryService.createTagCategory(user.getUserId(), dto);
    }

    @PatchMapping("/{categoryId}")
    public TagCategoryDto updateTagCategory(
        @PathVariable Long categoryId,
        @Validated @RequestBody UpdateTagCategoryDto dto
    ) {
        return tagCategoryService.updateTagCategory(categoryId, dto);
    }

    @GetMapping("/{categoryId}/tags")
    public Collection<TagDto> getTagsByCategory(@PathVariable Long categoryId) {
        return tagService.getTagsByCategory(categoryId);
    }

    @DeleteMapping("/{categoryId}")
    public void deleteTagCategory(@PathVariable Long categoryId) {
        tagCategoryService.deleteTagCategory(categoryId);
    }

    @PostMapping("/{categoryId}/tags")
    public TagDto addTagToCategory(@PathVariable Long categoryId, @RequestParam String tag) {
        return tagService.addTagToCategory(categoryId, tag);
    }

    @DeleteMapping("/{categoryId}/tags/{tagId}")
    public void deleteCategoryTag(@PathVariable Long categoryId, @PathVariable Long tagId) {
        tagService.deleteCategoryTag(tagId);
    }

    @GetMapping("/tags/search")
    public Page<TagProjection> searchTags(
        @AuthenticationPrincipal CurrentUser user,
        @RequestParam(name = "searchTerm", required = false, defaultValue = "") String searchTerm,
        @ParameterObject Pageable pageable
    ) {
        return tagService.searchTags(user, searchTerm, pageable);
    }

}
