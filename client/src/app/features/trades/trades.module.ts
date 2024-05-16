import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {TradesRoutingModule} from './trades-routing.module';
import {UploadExportedTradesComponent} from './components/upload-exported-trades/upload-exported-trades.component';
import {UploadFileService} from './services/upload-file.service';
import {MatButtonModule} from '@angular/material/button';
import {TradesContainerComponent} from './containers/trades-container/trades-container.component';
import {TradeService} from './services/trade.service';
import {DatatableActionsBarComponent} from './components/datatable-actions-bar/datatable-actions-bar.component';
import {MatIconModule} from '@angular/material/icon';
import {TradeDialogComponent} from './containers/trade-dialog/trade-dialog.component';
import {MatDialogModule} from '@angular/material/dialog';
import {ReactiveFormsModule} from '@angular/forms';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatSelectModule} from '@angular/material/select';
import {NgxMatSelectSearchModule} from 'ngx-mat-select-search';
import {TradingInstrumentService} from './services/trading-instrument.service';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatNativeDateModule} from '@angular/material/core';
import {AssignTradeAttributesComponent} from './components/assign-trade-attributes/assign-trade-attributes.component';
import {CurrencyService} from './services/currency.service';
import {BrokerService} from './services/broker.service';
import {MatTableModule} from '@angular/material/table';
import {MatPaginatorModule} from '@angular/material/paginator';
import {MatSortModule} from '@angular/material/sort';
import {MatCheckboxModule} from '@angular/material/checkbox';


@NgModule({
    declarations: [
        UploadExportedTradesComponent,
        TradesContainerComponent,
        DatatableActionsBarComponent,
        TradeDialogComponent,
        AssignTradeAttributesComponent,
    ],
    imports: [
        CommonModule,
        TradesRoutingModule,
        MatButtonModule,
        MatIconModule,
        MatDialogModule,
        ReactiveFormsModule,
        MatFormFieldModule,
        MatInputModule,
        MatSelectModule,
        NgxMatSelectSearchModule,
        MatDatepickerModule,
        MatNativeDateModule,
        MatTableModule,
        MatPaginatorModule,
        MatSortModule,
        MatCheckboxModule
    ],
    providers: [
        UploadFileService,
        TradeService,
        TradingInstrumentService,
        CurrencyService,
        BrokerService
    ]
})
export class TradesModule {
}
