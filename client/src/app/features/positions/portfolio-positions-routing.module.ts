import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { PositionsContainerComponent } from './containers/positions-container/positions-container.component';

const routes: Routes = [
  {
    path: '',
    component: PositionsContainerComponent
  }
];

@NgModule({
  imports: [RouterModule.forChild(routes)],
  exports: [RouterModule]
})
export class PortfolioPositionsRoutingModule {
}
