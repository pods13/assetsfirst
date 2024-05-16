import {ChangeDetectionStrategy, Component, OnDestroy, OnInit} from '@angular/core';
import {DashboardCardStore} from '../../cards/services/dashboard-card.store';
import {RxStompService} from '../../../../core/services/rx-stomp.service';

@Component({
    selector: 'app-portfolio-view',
    template: `
    <app-portfolio-dashboard></app-portfolio-dashboard>
  `,
    styleUrls: ['./portfolio-view.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush,
    providers: [
        DashboardCardStore
    ]
})
export class PortfolioViewComponent implements OnInit, OnDestroy {

    constructor(private rxStomp: RxStompService) {
    }

    ngOnInit(): void {
        this.rxStomp.activate();
    }

    async ngOnDestroy(): Promise<void> {
        await this.rxStomp.deactivate();
    }

}
