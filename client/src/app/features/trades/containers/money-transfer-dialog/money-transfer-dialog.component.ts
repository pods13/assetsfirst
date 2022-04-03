import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MoneyTradeOperation } from '../../types/money/money-trade-operation';
import { AddMoneyTradeDto } from '../../types/money/add-money-trade.dto';
import { CurrencyService } from '../../services/currency.service';
import { TradeCategory } from '../../types/trade-category';

@Component({
  selector: 'app-money-transfer-dialog',
  template: `
    <h1 mat-dialog-title>{{data?.title}}</h1>
    <div mat-dialog-content>
      <form [formGroup]="form">
        <div class="">
          <mat-form-field appearance="fill">
            <mat-label>Amount</mat-label>
            <input matInput [formControlName]="'amount'" type="number">
          </mat-form-field>

          <mat-form-field appearance="fill">
            <mat-label>Currency Code</mat-label>
            <mat-select [formControlName]="'currencyCode'">
              <mat-option *ngFor="let currencyCode of currencyCodes$ | async" [value]="currencyCode">
                {{currencyCode}}
              </mat-option>
            </mat-select>
          </mat-form-field>
        </div>
        <mat-form-field appearance="fill">
          <mat-label>Date</mat-label>
          <input matInput [matDatepicker]="picker" [formControlName]="'date'">
          <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
        </mat-form-field>
      </form>
    </div>
    <div mat-dialog-actions>
      <button mat-button [disabled]="form.invalid" (click)="transferMoney()">Save</button>
    </div>
  `,
  styleUrls: ['./money-transfer-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class MoneyTransferDialogComponent implements OnInit {

  form: FormGroup;

  currencyCodes$ = this.currencyService.getAvailableCurrencyCodes();

  constructor(public dialogRef: MatDialogRef<MoneyTransferDialogComponent, AddMoneyTradeDto>,
              private fb: FormBuilder,
              @Inject(MAT_DIALOG_DATA) public data: DialogData,
              private currencyService: CurrencyService) {
    this.form = this.fb.group({
      operation: this.fb.control(data.operation, Validators.compose([Validators.required])),
      date: this.fb.control(new Date(), Validators.compose([Validators.required])),
      amount: this.fb.control('', Validators.compose([Validators.required])),
      currencyCode: this.fb.control('RUB', Validators.compose([Validators.required])),
    });
  }

  ngOnInit(): void {
  }

  transferMoney() {
    const {operation, date, amount, currencyCode} = this.form.value;
    const dto = {operation, date, amount, currencyCode, tradeCategory: TradeCategory.MONEY} as AddMoneyTradeDto;
    this.dialogRef.close(dto);
  }
}

interface DialogData {
  title: string;
  operation: MoneyTradeOperation;
}
