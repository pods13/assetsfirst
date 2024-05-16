import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {DashboardCard} from '../types/dashboard-card';
import {RxStompService} from '../../../../core/services/rx-stomp.service';

@Injectable()
export class CardService {

    constructor(private http: HttpClient,
                private rxStompService: RxStompService) {
    }

    addCard(dashboardId: number, card: DashboardCard) {
        this.manageCard(dashboardId, card, 'add');
    }

    updateCards(dashboardId: number, cards: DashboardCard[]) {
        this.manageCard(dashboardId, cards, 'update');
    }

    deleteCard(dashboardId: number, card: DashboardCard) {
        this.manageCard(dashboardId, card, 'delete');
    }

    private manageCard(dashboardId: number, card: DashboardCard | DashboardCard[], operation: string) {
        this.rxStompService.publish({
            destination: `/app/${dashboardId}/cards/${operation}`,
            body: JSON.stringify(card),
        });
    }

}
