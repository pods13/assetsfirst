import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PortfolioHoldingsRoutingModule } from './portfolio-holdings-routing.module';
import { HoldingsContainerComponent } from './containers/holdings-container/holdings-container.component';


@NgModule({
  declarations: [
    HoldingsContainerComponent
  ],
  imports: [
    CommonModule,
    PortfolioHoldingsRoutingModule
  ]
})
export class PortfolioHoldingsModule { }
