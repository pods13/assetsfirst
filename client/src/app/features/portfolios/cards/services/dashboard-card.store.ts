import { Store } from '../../../../core/services/store';
import { Injectable } from '@angular/core';
import { DashboardCardStoreState } from './dashboard-card-store.state';
import { map } from 'rxjs';
import { CardService } from './card.service';
import { DashboardCard } from '../types/dashboard-card';
import { createCard } from '../helpers/create-card.helper';

@Injectable()
export class DashboardCardStore extends Store<DashboardCardStoreState> {

  cardsByItems$ = this.state$.pipe(
    map(state => ({cards: state.cards, items: state.cards.map(card => ({...card}))}))
  );

  constructor(private cardService: CardService) {
    super(new DashboardCardStoreState());
  }

  init(dashboardId: number, cards: DashboardCard[]) {
    const upgradedCards = cards.map(card => ({...createCard(card.containerType), ...card}));
    this.setState({...this.state, dashboardId, cards: upgradedCards});
  }

  addCard(card: DashboardCard) {
    const {dashboardId} = this.state;
    this.cardService.addCard(dashboardId, card);
    this.setState({...this.state, cards: [...this.state.cards, card]});
  }

  updateCard(cardToUpdate: DashboardCard) {
    const {dashboardId} = this.state;
    const cardsNewState = this.state.cards.map(c => c.id === cardToUpdate.id ? cardToUpdate : c);
    this.cardService.updateCards(dashboardId, cardsNewState);
    this.setState({
      ...this.state,
      cards: cardsNewState
    });
  }

  deleteCard(cardToDelete: DashboardCard) {
    const {dashboardId} = this.state;
    this.cardService.deleteCard(dashboardId, cardToDelete);
    this.setState({
      ...this.state,
      cards: [...this.state.cards.filter(card => card.id !== cardToDelete.id)]
    });
  }
}
