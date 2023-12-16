import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PositionsContainerV2Component} from './containers/positions-container-v2/positions-container-v2.component';

const routes: Routes = [
    {
        path: '',
        component: PositionsContainerV2Component
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class PortfolioPositionsRoutingModule {
}
