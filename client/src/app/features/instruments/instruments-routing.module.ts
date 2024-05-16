import {NgModule} from '@angular/core';
import {RouterModule, Routes} from '@angular/router';
import {InstrumentViewComponent} from './views/instrument-view/instrument-view.component';

const routes: Routes = [
    {
        path: ':identifier',
        component: InstrumentViewComponent
    }
];

@NgModule({
    imports: [RouterModule.forChild(routes)],
    exports: [RouterModule]
})
export class InstrumentsRoutingModule {
}
