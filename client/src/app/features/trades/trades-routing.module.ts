import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {TradesContainerComponent} from './containers/trades-container/trades-container.component';

const routes: Routes = [
    {
        path: '',
        component: TradesContainerComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class TradesRoutingModule {
}
