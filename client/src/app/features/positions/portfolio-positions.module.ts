import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PortfolioPositionsRoutingModule } from './portfolio-positions-routing.module';
import { PositionsContainerComponent } from './containers/positions-container/positions-container.component';
import { PortfolioPositionService } from './services/portfolio-position.service';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';


@NgModule({
  declarations: [
    PositionsContainerComponent
  ],
  imports: [
    CommonModule,
    PortfolioPositionsRoutingModule,
    NgxDatatableModule
  ],
  providers: [
    PortfolioPositionService,
  ]
})
export class PortfolioPositionsModule {
}
