import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {PortfolioViewComponent} from './views/portfolio-view/portfolio-view.component';

const routes: Routes = [{
    path: '',
    component: PortfolioViewComponent
}];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class PortfoliosRoutingModule {
}
