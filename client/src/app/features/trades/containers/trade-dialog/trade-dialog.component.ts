import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { UntypedFormBuilder, UntypedFormGroup, Validators } from '@angular/forms';
import {
  BehaviorSubject,
  catchError,
  debounceTime,
  distinctUntilChanged,
  filter,
  map,
  of,
  ReplaySubject,
  switchMap,
  tap
} from 'rxjs';
import { TradingInstrumentService } from '../../services/trading-instrument.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TradeViewDto } from '../../types/trade-view.dto';
import { EditTradeDto } from '../../types/edit-trade.dto';
import { AddTradeDto } from '../../types/add-trade.dto';
import { BrokerService } from '../../services/broker.service';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-trade-dialog',
  template: `
    <h1 mat-dialog-title>{{data?.title}}</h1>
    <div mat-dialog-content>
      <form [formGroup]="form">
        <mat-form-field appearance="fill">
          <mat-select [formControlName]="'instrument'" placeholder="Ticker or company name"
                      [compareWith]="compareFunction">
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
        <mat-form-field appearance="fill">
          <mat-label>Broker</mat-label>
          <mat-select [formControlName]="'brokerId'">
            <mat-option *ngFor="let broker of brokers$ | async" [value]="broker.id">
              {{broker.name}}
            </mat-option>
          </mat-select>
        </mat-form-field>
        <app-assign-trade-attributes [trade]="data?.trade"></app-assign-trade-attributes>
      </form>
      <div class="total">Total: {{total$ | async}}</div>
    </div>
    <div mat-dialog-actions>
      <button mat-button [disabled]="form.invalid" (click)="saveTrade()">Save</button>
    </div>
  `,
  styleUrls: ['./trade-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TradeDialogComponent implements OnInit {

  form: UntypedFormGroup;

  searching = false;

  filteredInstruments: ReplaySubject<any[]> = new ReplaySubject<any[]>(1);

  brokers$ = this.brokerService.getBrokers();
  total$ = new BehaviorSubject(0.0);

  constructor(private fb: UntypedFormBuilder,
              private tradingInstrumentService: TradingInstrumentService,
              private brokerService: BrokerService,
              public dialogRef: MatDialogRef<TradeDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: { title: string; trade?: TradeViewDto; }) {
    const trade = data?.trade;
    const instrument = trade ? this.composeInstrument(trade) : null;
    if (instrument) {
      this.filteredInstruments.next([instrument]);
    }
    this.form = this.fb.group({
      instrument: this.fb.control({
        value: instrument,
        disabled: trade ?? false
      }, Validators.compose([Validators.required])),
      instrumentFilter: this.fb.control(''),
      brokerId: this.fb.control(trade?.brokerId, Validators.compose([Validators.required])),
    });
    this.form.valueChanges.pipe(untilDestroyed(this),
      map(({specifics}) => {
        return specifics.price * specifics.quantity;
      }))
      .subscribe(this.total$);
  }

  private composeInstrument(trade: TradeViewDto) {
    return {id: trade.instrumentId, instrumentType: trade.instrumentType, ticker: trade.symbol, name: trade.name};
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

  compareFunction(o1: any, o2: any) {
    return o1?.id === o2?.id;
  }

  saveTrade() {
    if (this.data?.trade) {
      const {specifics, brokerId} = this.form.value;
      const {date, price, quantity} = specifics;
      const offsetDate = this.getTimeZoneOffsetDate(date);
      this.dialogRef.close({
        tradeId: this.data.trade.id,
        instrumentId: this.data.trade.instrumentId,
        instrumentType: this.data.trade.instrumentType,
        date: offsetDate,
        price,
        quantity,
        brokerId
      } as EditTradeDto)
    } else {
      const {instrument, specifics, brokerId} = this.form.value;
      const {operation, date, price, quantity} = specifics;
      const offsetDate = this.getTimeZoneOffsetDate(date);
      this.dialogRef.close({
        instrumentId: instrument.id,
        instrumentType: instrument.instrumentType,
        operation,
        date: offsetDate,
        price,
        quantity,
        brokerId
      } as AddTradeDto);
    }
  }

  getTimeZoneOffsetDate(date: Date): Date {
    const res = new Date(date);
    res.setMinutes(res.getMinutes() - res.getTimezoneOffset());
    return res;
  }
}
