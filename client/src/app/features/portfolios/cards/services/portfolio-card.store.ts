import { Store } from '../../../../core/services/store';
import { Injectable } from '@angular/core';
import { PortfolioCardStoreState } from './portfolio-card.store.state';
import { map } from 'rxjs';
import { CardService } from './card.service';
import { PortfolioCardDto } from '../types/portfolio-card.dto';

@Injectable()
export class PortfolioCardStore extends Store<PortfolioCardStoreState> {

  cardsByItems$ = this.state$.pipe(
    map(state => ({cards: state.cards, items: state.cards.map(card => ({...card}))}))
  );

  constructor(private cardService: CardService) {
    super(new PortfolioCardStoreState());
  }

  init(portfolioId: number, cards: PortfolioCardDto[]) {
    this.setState({...this.state, id: portfolioId, cards: [...cards]});
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
