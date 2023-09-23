package com.topably.assets.portfolios.domain.cards.input.allocation;

import java.io.Serializable;

public record TagWithCategoryDto(Long id, String name, Long categoryId, String categoryName) implements Serializable {

}
