import { Store } from '../../../core/services/store';
import { Injectable } from '@angular/core';
import { PortfolioStoreState } from './portfolio.store.state';
import { PortfolioService } from './portfolio.service';
import { map, of, switchMap, tap } from 'rxjs';
import { CardService } from '../cards/services/card.service';
import { PortfolioCardDto } from '../cards/types/portfolio-card.dto';

@Injectable()
export class PortfolioStore extends Store<PortfolioStoreState> {

  cardsByItems$ = this.state$.pipe(
    map(state => ({cards: state.cards, items: state.cards.map(card => ({...card}))}))
  );

  constructor(private portfolioService: PortfolioService,
              private cardService: CardService) {
    super(new PortfolioStoreState());
  }

  init() {
    const createPortfolio$ = this.portfolioService.addPortfolio({cards: []});
    return this.portfolioService.getUserPortfolios().pipe(
      switchMap(portfolios => portfolios.length ? of(portfolios[0]) : createPortfolio$),
      tap(portfolio => this.setState({...this.state, id: portfolio.id, cards: [...portfolio.cards]})),
    )
  }

  addCard(card: PortfolioCardDto) {
    const {id} = this.state;
    this.cardService.addCard(id, card);
    this.setState({...this.state, cards: [...this.state.cards, card]});
  }

  updateCard(cardToUpdate: PortfolioCardDto) {
    const {id} = this.state;
    this.cardService.updateCard(id, cardToUpdate);
    this.setState({
      ...this.state,
      cards: [...this.state.cards.filter(card => card.id !== cardToUpdate.id), {...cardToUpdate}]
    });
  }

  deleteCard(cardToDelete: PortfolioCardDto) {
    const {id} = this.state;
    this.cardService.deleteCard(id, cardToDelete);
    this.setState({
      ...this.state,
      cards: [...this.state.cards.filter(card => card.id !== cardToDelete.id)]
    });
  }
}
