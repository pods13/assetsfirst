import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { DashboardCardStore } from '../../cards/services/dashboard-card-store.service';

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
export class PortfolioViewComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
