import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { HoldingsContainerComponent } from './containers/holdings-container/holdings-container.component';

const routes: Routes = [
  {
    path: '',
    component: HoldingsContainerComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PortfolioHoldingsRoutingModule {
}
