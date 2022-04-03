package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.cards.PortfolioCard;

public interface PortfolioCardService {

    void addCard(Long portfolioId, PortfolioCard card);

    void updateCard(Long portfolioId, PortfolioCard cardToUpdate);

    void deleteCard(Long portfolioId, PortfolioCard cardToDelete);
}
