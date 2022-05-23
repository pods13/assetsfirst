import { Store } from '../../../../core/services/store';
import { Injectable } from '@angular/core';
import { DashboardCardStoreState } from './dashboard-card-store.state';
import { map } from 'rxjs';
import { CardService } from './card.service';
import { DashboardCard } from '../types/dashboard-card';

@Injectable()
export class DashboardCardStore extends Store<DashboardCardStoreState> {

  cardsByItems$ = this.state$.pipe(
    map(state => ({cards: state.cards, items: state.cards.map(card => ({...card}))}))
  );

  constructor(private cardService: CardService) {
    super(new DashboardCardStoreState());
  }

  init(dashboardId: number, cards: DashboardCard[]) {
    //TODO go through cards and propagate into them possible changes to their state structure, i.e. new property
    this.setState({...this.state, dashboardId, cards: [...cards]});
  }

  addCard(card: DashboardCard) {
    const {dashboardId} = this.state;
    this.cardService.addCard(dashboardId, card);
    this.setState({...this.state, cards: [...this.state.cards, card]});
  }

  updateCard(cardToUpdate: DashboardCard) {
    const {dashboardId} = this.state;
    this.cardService.updateCard(dashboardId, cardToUpdate);
    this.setState({
      ...this.state,
      cards: [...this.state.cards.filter(card => card.id !== cardToUpdate.id), {...cardToUpdate}]
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
