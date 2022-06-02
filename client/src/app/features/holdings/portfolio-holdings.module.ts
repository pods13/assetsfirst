import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PortfolioHoldingsRoutingModule } from './portfolio-holdings-routing.module';
import { HoldingsContainerComponent } from './containers/holdings-container/holdings-container.component';
import { PortfolioHoldingService } from './services/portfolio-holding.service';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { FundamentalsService } from './services/fundamentals.service';


@NgModule({
  declarations: [
    HoldingsContainerComponent
  ],
  imports: [
    CommonModule,
    PortfolioHoldingsRoutingModule,
    NgxDatatableModule
  ],
  providers: [
    PortfolioHoldingService,
    FundamentalsService
  ]
})
export class PortfolioHoldingsModule {
}
