import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {InstrumentsRoutingModule} from './instruments-routing.module';
import {InstrumentViewComponent} from './views/instrument-view/instrument-view.component';
import {InstrumentContainerComponent} from './containers/instrument-container/instrument-container.component';
import {InstrumentService} from './services/instrument.service';


@NgModule({
    declarations: [
        InstrumentViewComponent,
        InstrumentContainerComponent
    ],
    imports: [
        CommonModule,
        InstrumentsRoutingModule
    ],
    providers: [InstrumentService]
})
export class InstrumentsModule {
}
