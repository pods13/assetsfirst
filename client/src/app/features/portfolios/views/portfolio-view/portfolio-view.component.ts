import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { PortfolioStore } from '../../services/portfolio.store';

@Component({
  selector: 'app-portfolio-view',
  template: `
    <app-portfolio-dashboard></app-portfolio-dashboard>
  `,
  styleUrls: ['./portfolio-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [
    PortfolioStore
  ]
})
export class PortfolioViewComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
