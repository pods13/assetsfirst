import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { PortfolioCardStore } from '../../cards/services/portfolio-card.store';

@Component({
  selector: 'app-portfolio-view',
  template: `
    <app-portfolio-dashboard></app-portfolio-dashboard>
  `,
  styleUrls: ['./portfolio-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    PortfolioCardStore
  ]
})
export class PortfolioViewComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
