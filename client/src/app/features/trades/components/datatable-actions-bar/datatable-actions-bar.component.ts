import { ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { SecurityTradeDialogComponent } from '../../containers/security-trade-dialog/security-trade-dialog.component';
import { filter } from 'rxjs';
import { AddSecurityTradeDto } from '../../types/add-security-trade.dto';
import { MoneyTransferDialogComponent } from '../../containers/money-transfer-dialog/money-transfer-dialog.component';
import { MoneyTradeOperation } from '../../types/money/money-trade-operation';
import { AddMoneyTradeDto } from '../../types/money/add-money-trade.dto';
import { AddTradeDto } from '../../types/add-trade.dto';

@Component({
  selector: 'app-datatable-actions-bar',
  template: `
    <button mat-button (click)="openDepositDialog()">
      <mat-icon>import_export</mat-icon>
      {{'Deposit'}}
    </button>
    <button mat-button (click)="openWithdrawalDialog()">
      <mat-icon>import_export</mat-icon>
      {{'Withdrawal'}}
    </button>
    <button mat-button (click)="openSecurityTradeDialog()">
      <mat-icon>add</mat-icon>
      {{'Add Trade'}}
    </button>
  `,
  styleUrls: ['./datatable-actions-bar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatatableActionsBarComponent implements OnInit {

  @Output()
  trade = new EventEmitter<AddTradeDto>();

  constructor(private dialog: MatDialog) {
  }

  ngOnInit(): void {
  }

  openDepositDialog() {
    this.openTransferDialog({
      title: 'Deposit Money',
      operation: MoneyTradeOperation.DEPOSIT
    });

  }

  openWithdrawalDialog() {
    this.openTransferDialog({
      title: 'Withdrawal Money',
      operation: MoneyTradeOperation.WITHDRAWAL
    });
  }

  private openTransferDialog(dialogData: any) {
    const dialogRef = this.dialog.open<MoneyTransferDialogComponent, any, AddMoneyTradeDto>(MoneyTransferDialogComponent, {
      data: dialogData
    });

    dialogRef.afterClosed()
      .pipe(filter(res => !!res))
      .subscribe(result => {
        this.trade.emit(result);
      });
  }

  openSecurityTradeDialog() {
    const dialogRef = this.dialog.open<SecurityTradeDialogComponent, any, AddSecurityTradeDto>(SecurityTradeDialogComponent, {});

    dialogRef.afterClosed()
      .pipe(filter(res => !!res))
      .subscribe(result => {
        this.trade.emit(result);
      });
  }
}
