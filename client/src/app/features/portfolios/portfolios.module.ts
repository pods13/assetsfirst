import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { PortfolioViewComponent } from './views/portfolio-view/portfolio-view.component';
import { PortfolioDashboardComponent } from './containers/portfolio-dashboard/portfolio-dashboard.component';
import { GridsterModule } from 'angular-gridster2';
import { DashboardCardsModule } from './cards/dashboard-cards.module';
import { PortfoliosRoutingModule } from './portfolios-routing.module';
import { PortfolioDashboardService } from './services/portfolio-dashboard.service';
import { DashboardActionsBarComponent } from './components/dashboard-actions-bar/dashboard-actions-bar.component';
import { MatButtonModule } from '@angular/material/button';


@NgModule({
  declarations: [
    PortfolioViewComponent,
    PortfolioDashboardComponent,
    DashboardActionsBarComponent
  ],
    imports: [
        CommonModule,
        PortfoliosRoutingModule,
        GridsterModule,
        DashboardCardsModule,
        MatButtonModule
    ],
  providers: [
    PortfolioDashboardService
  ]
})
export class PortfoliosModule {
}
