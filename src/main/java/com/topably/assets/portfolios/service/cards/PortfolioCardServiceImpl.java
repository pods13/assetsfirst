package com.topably.assets.portfolios.service.cards;

import com.topably.assets.portfolios.domain.Portfolio;
import com.topably.assets.portfolios.domain.cards.PortfolioCard;
import com.topably.assets.portfolios.repository.PortfolioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PortfolioCardServiceImpl implements PortfolioCardService {

    private final PortfolioRepository portfolioRepository;

    @Override
    @Transactional
    public void addCard(Long portfolioId, PortfolioCard card) {
        Portfolio portfolio = portfolioRepository.getById(portfolioId);
        portfolio.getCards().add(card);
        portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public void updateCard(Long portfolioId, PortfolioCard cardToUpdate) {
        Portfolio portfolio = portfolioRepository.getById(portfolioId);
        Set<PortfolioCard> cards = portfolio.getCards().stream()
                .filter(card -> !card.getId().equals(cardToUpdate.getId())).collect(Collectors.toSet());
        cards.add(cardToUpdate);
        portfolio.setCards(cards);
        portfolioRepository.save(portfolio);
    }

    @Override
    @Transactional
    public void deleteCard(Long portfolioId, PortfolioCard cardToDelete) {
        Portfolio portfolio = portfolioRepository.getById(portfolioId);
        portfolio.getCards().remove(cardToDelete);
        portfolioRepository.save(portfolio);
    }
}
