import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TradesRoutingModule } from './trades-routing.module';
import { UploadExportedTradesComponent } from './components/upload-exported-trades/upload-exported-trades.component';
import { UploadFileService } from './services/upload-file.service';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { TradesContainerComponent } from './containers/trades-container/trades-container.component';
import { TradeService } from './services/trade.service';
import { DatatableActionsBarComponent } from './components/datatable-actions-bar/datatable-actions-bar.component';
import { MatIconModule } from '@angular/material/icon';
import { TradeDialogComponent } from './containers/trade-dialog/trade-dialog.component';
import { MatLegacyDialogModule as MatDialogModule } from '@angular/material/legacy-dialog';
import { ReactiveFormsModule } from '@angular/forms';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import { MatLegacySelectModule as MatSelectModule } from '@angular/material/legacy-select';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { TradingInstrumentService } from './services/trading-instrument.service';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AssignTradeAttributesComponent } from './components/assign-trade-attributes/assign-trade-attributes.component';
import { CurrencyService } from './services/currency.service';
import { BrokerService } from './services/broker.service';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { MatLegacyPaginatorModule as MatPaginatorModule } from '@angular/material/legacy-paginator';
import { MatSortModule } from '@angular/material/sort';
import { MatLegacyCheckboxModule as MatCheckboxModule } from '@angular/material/legacy-checkbox';


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
