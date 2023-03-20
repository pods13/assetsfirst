import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ControlContainer, UntypedFormBuilder, UntypedFormGroup, FormGroupDirective, Validators } from '@angular/forms';
import { TradeOperation } from '../../types/trade-operation';
import { TradeViewDto } from '../../types/trade-view.dto';

@Component({
  selector: 'app-assign-trade-attributes',
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
  styleUrls: ['./assign-trade-attributes.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  viewProviders: [
    {provide: ControlContainer, useExisting: FormGroupDirective}
  ]
})
export class AssignTradeAttributesComponent implements OnInit {

  @Input()
  trade?: TradeViewDto;

  form!: UntypedFormGroup;

  tradeOperations = Object.keys(TradeOperation);

  constructor(public parentForm: FormGroupDirective,
              private fb: UntypedFormBuilder) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      operation: this.fb.control({
        value: this.trade?.operation ?? TradeOperation.BUY, disabled: this.trade ?? false
      }, Validators.compose([Validators.required])),
      date: this.fb.control(this.trade?.date ?? new Date(), Validators.compose([Validators.required])),
      price: this.fb.control(this.trade?.price ?? '', Validators.compose([Validators.required])),
      quantity: this.fb.control(this.trade?.quantity ?? '', Validators.compose([Validators.required])),
    });
    this.parentForm.form.addControl('specifics', this.form);
  }

}
