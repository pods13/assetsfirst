package com.topably.assets.portfolios;

import com.topably.assets.auth.domain.security.CurrentUser;
import com.topably.assets.portfolios.domain.dto.tag.CreateTagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagCategoryDto;
import com.topably.assets.portfolios.domain.dto.tag.TagDto;
import com.topably.assets.portfolios.service.tag.TagCategoryService;
import com.topably.assets.portfolios.service.tag.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collection;

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
    public TagCategoryDto createTagCategory(@AuthenticationPrincipal CurrentUser user,
                                            @Validated @RequestBody CreateTagCategoryDto dto) {
        return tagCategoryService.createTagCategory(user.getUserId(), dto);
    }

    @GetMapping("/{categoryId}/tags")
    public Collection<TagDto> getTagsByCategory(@PathVariable Long categoryId) {
        return tagService.getTagsByCategory(categoryId);
    }
}
