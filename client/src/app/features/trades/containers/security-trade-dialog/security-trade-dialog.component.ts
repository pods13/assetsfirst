import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { catchError, debounceTime, distinctUntilChanged, filter, of, ReplaySubject, switchMap, tap } from 'rxjs';
import { SecurityService } from '../../services/security.service';
import { MatDialogRef } from '@angular/material/dialog';
import { AddSecurityTradeDto } from '../../types/add-security-trade.dto';

@Component({
  selector: 'app-security-trade-dialog',
  template: `
    <h1 mat-dialog-title>Add New Trade</h1>
    <div mat-dialog-content>
      <form [formGroup]="form">
        <mat-form-field>
          <mat-select [formControlName]="'security'" placeholder="Ticker or company name">
            <mat-option>
              <ngx-mat-select-search [formControlName]="'securityFilter'"
                                     placeholderLabel="Find security..."
                                     noEntriesFoundLabel="no matching security found"
                                     [searching]="searching">
              </ngx-mat-select-search>
            </mat-option>
            <mat-option *ngFor="let security of filteredSecurities | async"
                        [value]="{id: security.id, securityType: security.securityType}">
              {{security.ticker + ' (' + security.name + ')'}}
            </mat-option>
          </mat-select>
        </mat-form-field>
        <app-assign-stock-etf-trade-specifics></app-assign-stock-etf-trade-specifics>
      </form>
    </div>
    <div mat-dialog-actions>
      <button mat-button [disabled]="form.invalid" (click)="tradeSecurity()">Save</button>
    </div>
  `,
  styleUrls: ['./security-trade-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SecurityTradeDialogComponent implements OnInit {

  form: FormGroup;

  searching = false;

  filteredSecurities: ReplaySubject<any[]> = new ReplaySubject<any[]>(1);

  constructor(private fb: FormBuilder,
              private securityService: SecurityService,
              public dialogRef: MatDialogRef<SecurityTradeDialogComponent, AddSecurityTradeDto>,) {
    this.form = this.fb.group({
      security: this.fb.control('', Validators.compose([Validators.required])),
      securityFilter: this.fb.control(''),
    });
  }

  ngOnInit(): void {
    this.form.get('securityFilter')?.valueChanges
      .pipe(
        filter(search => !!search),
        tap(() => this.searching = true),
        debounceTime(400),
        distinctUntilChanged(),
        switchMap(search => {
          return this.securityService.searchSecuritiesByNameOrTicker(search);
        }),
        catchError((err, caught) => {
          this.searching = false;
          return of([]);
        })
      ).subscribe(securities => {
      this.filteredSecurities.next(securities);
      this.searching = false;
    });
  }

  tradeSecurity() {
    console.log(this.form.value)
    const {security, specifics} = this.form.value;
    const {operation, date, price, quantity} = specifics;
    this.dialogRef.close({
      securityId: security.id,
      tradeCategory: security.securityType,
      operation,
      date,
      price,
      quantity
    });
  }
}
