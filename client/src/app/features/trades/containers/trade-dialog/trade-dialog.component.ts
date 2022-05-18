import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { catchError, debounceTime, distinctUntilChanged, filter, of, ReplaySubject, switchMap, tap } from 'rxjs';
import { TradingInstrumentService } from '../../services/trading-instrument.service';
import { MatDialogRef } from '@angular/material/dialog';
import { AddTradeDto } from '../../types/add-trade.dto';

@Component({
  selector: 'app-trade-dialog',
  template: `
    <h1 mat-dialog-title>Add New Trade</h1>
    <div mat-dialog-content>
      <form [formGroup]="form">
        <mat-form-field>
          <mat-select [formControlName]="'instrument'" placeholder="Ticker or company name">
            <mat-option>
              <ngx-mat-select-search [formControlName]="'instrumentFilter'"
                                     placeholderLabel="Find instrument..."
                                     noEntriesFoundLabel="no matching trading instrument found"
                                     [searching]="searching">
              </ngx-mat-select-search>
            </mat-option>
            <mat-option *ngFor="let instrument of filteredInstruments | async"
                        [value]="{id: instrument.id, instrumentType: instrument.instrumentType}">
              {{instrument.ticker + (instrument.name ? ' (' + instrument.name + ')' : '')}}
            </mat-option>
          </mat-select>
        </mat-form-field>
        <app-assign-trade-attributes></app-assign-trade-attributes>
      </form>
    </div>
    <div mat-dialog-actions>
      <button mat-button [disabled]="form.invalid" (click)="executeTrade()">Save</button>
    </div>
  `,
  styleUrls: ['./trade-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TradeDialogComponent implements OnInit {

  form: FormGroup;

  searching = false;

  filteredInstruments: ReplaySubject<any[]> = new ReplaySubject<any[]>(1);

  constructor(private fb: FormBuilder,
              private tradingInstrumentService: TradingInstrumentService,
              public dialogRef: MatDialogRef<TradeDialogComponent, AddTradeDto>,) {
    this.form = this.fb.group({
      instrument: this.fb.control('', Validators.compose([Validators.required])),
      instrumentFilter: this.fb.control(''),
    });
  }

  ngOnInit(): void {
    this.form.get('instrumentFilter')?.valueChanges
      .pipe(
        filter(search => !!search),
        tap(() => this.searching = true),
        debounceTime(400),
        distinctUntilChanged(),
        switchMap(search => {
          return this.tradingInstrumentService.searchInstrumentsByNameOrTicker(search);
        }),
        catchError((err, caught) => {
          this.searching = false;
          return of([]);
        })
      ).subscribe(instruments => {
      this.filteredInstruments.next(instruments);
      this.searching = false;
    });
  }

  executeTrade() {
    console.log(this.form.value)
    const {instrument, specifics} = this.form.value;
    const {operation, date, price, quantity} = specifics;
    this.dialogRef.close({
      instrumentId: instrument.id,
      instrumentType: instrument.instrumentType,
      operation,
      date,
      price,
      quantity
    });
  }
}
