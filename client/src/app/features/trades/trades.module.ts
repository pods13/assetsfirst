import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { TradesRoutingModule } from './trades-routing.module';
import { UploadExportedTradesComponent } from './components/upload-exported-trades/upload-exported-trades.component';
import { UploadFileService } from './services/upload-file.service';
import { MatButtonModule } from '@angular/material/button';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { TradesContainerComponent } from './containers/trades-container/trades-container.component';
import { TradeService } from './services/trade.service';
import { DatatableActionsBarComponent } from './components/datatable-actions-bar/datatable-actions-bar.component';
import { MatIconModule } from '@angular/material/icon';
import { SecurityTradeDialogComponent } from './containers/security-trade-dialog/security-trade-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { ReactiveFormsModule } from '@angular/forms';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { NgxMatSelectSearchModule } from 'ngx-mat-select-search';
import { SecurityService } from './services/security.service';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatNativeDateModule } from '@angular/material/core';
import { AssignStockEtfTradeSpecificsComponent } from './components/assign-stock-etf-trade-specifics/assign-stock-etf-trade-specifics.component';
import { MoneyTransferDialogComponent } from './containers/money-transfer-dialog/money-transfer-dialog.component';
import { CurrencyService } from './services/currency.service';


@NgModule({
  declarations: [
    UploadExportedTradesComponent,
    TradesContainerComponent,
    DatatableActionsBarComponent,
    SecurityTradeDialogComponent,
    AssignStockEtfTradeSpecificsComponent,
    MoneyTransferDialogComponent
  ],
  imports: [
    CommonModule,
    TradesRoutingModule,
    MatButtonModule,
    NgxDatatableModule,
    MatIconModule,
    MatDialogModule,
    ReactiveFormsModule,
    MatFormFieldModule,
    MatInputModule,
    MatSelectModule,
    NgxMatSelectSearchModule,
    MatDatepickerModule,
    MatNativeDateModule
  ],
  providers: [
    UploadFileService,
    TradeService,
    SecurityService,
    CurrencyService
  ]
})
export class TradesModule {
}
