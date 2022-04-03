import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PortfolioViewComponent } from './views/portfolio-view/portfolio-view.component';
import { PortfolioDashboardComponent } from './containers/portfolio-dashboard/portfolio-dashboard.component';
import { GridsterModule } from 'angular-gridster2';
import { PortfolioCardsModule } from './cards/portfolio-cards.module';
import { PortfoliosRoutingModule } from './portfolios-routing.module';
import { PortfolioService } from './services/portfolio.service';
import { PortfolioStore } from './services/portfolio.store';
import { PortfolioActionsBarComponent } from './components/portfolio-actions-bar/portfolio-actions-bar.component';
import { MatButtonModule } from '@angular/material/button';


@NgModule({
  declarations: [
    PortfolioViewComponent,
    PortfolioDashboardComponent,
    PortfolioActionsBarComponent
  ],
    imports: [
        CommonModule,
        PortfoliosRoutingModule,
        GridsterModule,
        PortfolioCardsModule,
        MatButtonModule
    ],
  providers: [
    PortfolioService
  ]
})
export class PortfoliosModule {
}
