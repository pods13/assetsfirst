import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import {
  ControlContainer,
  FormBuilder,
  FormGroup,
  FormGroupDirective,
  FormGroupName,
  Validators
} from '@angular/forms';
import { TradeOperation } from '../../types/trade-operation';

@Component({
  selector: 'app-assign-stock-etf-trade-specifics',
  template: `
    <form [formGroup]="form">
      <div class="">
        <mat-form-field appearance="fill">
          <mat-label>Operation</mat-label>
          <mat-select [formControlName]="'operation'">
            <mat-option *ngFor="let operation of tradeOperations" [value]="operation">
              {{operation}}
            </mat-option>
          </mat-select>
        </mat-form-field>

        <mat-form-field appearance="fill">
          <mat-label>Trade Date</mat-label>
          <input matInput [matDatepicker]="picker" [formControlName]="'date'">
          <mat-datepicker-toggle matSuffix [for]="picker"></mat-datepicker-toggle>
          <mat-datepicker #picker></mat-datepicker>
        </mat-form-field>
      </div>

      <div class="">
        <mat-form-field appearance="fill">
          <mat-label>Price</mat-label>
          <input matInput [formControlName]="'price'" type="number">
        </mat-form-field>

        <mat-form-field appearance="fill">
          <mat-label>Quantity</mat-label>
          <input matInput [formControlName]="'quantity'" type="number">
        </mat-form-field>
      </div>
    </form>
  `,
  styleUrls: ['./assign-stock-etf-trade-specifics.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  viewProviders: [
    {provide: ControlContainer, useExisting: FormGroupDirective}
  ]
})
export class AssignStockEtfTradeSpecificsComponent implements OnInit {

  form!: FormGroup;

  tradeOperations = Object.keys(TradeOperation);

  constructor(public parentForm: FormGroupDirective,
              private fb: FormBuilder) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      operation: this.fb.control(TradeOperation.BUY, Validators.compose([Validators.required])),
      date: this.fb.control(new Date(), Validators.compose([Validators.required])),
      price: this.fb.control('', Validators.compose([Validators.required])),
      quantity: this.fb.control('', Validators.compose([Validators.required])),
    });
    this.parentForm.form.addControl('specifics', this.form);
  }

}
