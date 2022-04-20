import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { debounceTime, first, Observable, skip, tap, withLatestFrom } from 'rxjs';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { DividendGoalsCardData } from '../../types/out/dividend-goals-card-data';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { DividendGoalsCard } from '../../types/in/dividend-goals-card';
import { PortfolioCardStore } from '../../services/portfolio-card.store';
import { tapOnce } from '../../../../../core/helpers/tapOnce';

@UntilDestroy()
@Component({
  selector: 'app-dividend-goals-card',
  template: `
    <ng-container *ngIf="setupFormBeforeData$ | async as data">
      {{data.extraExpenses}}
      <form [formGroup]="form">
        <mat-list [formArrayName]="'desiredPositions'">
          <mat-list-item *ngFor="let item of data.items; let i = index;">
            <span>{{item.name}}</span>
            <mat-form-field>
              <input matInput type="number" [value]="item.quantity" [disabled]="true">
            </mat-form-field>
            <span> / </span>
            <mat-form-field>
              <input matInput type="number" [value]="item.quantity" [formControlName]="i">
            </mat-form-field>
          </mat-list-item>
        </mat-list>
      </form>
    </ng-container>
  `,
  styleUrls: ['./dividend-goals-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DividendGoalsCardComponent implements CardContainer<DividendGoalsCard, DividendGoalsCardData>, OnInit, AfterViewInit {

  card!: DividendGoalsCard;
  data$!: Observable<DividendGoalsCardData>;

  setupFormBeforeData$!: Observable<DividendGoalsCardData>;

  form: FormGroup;

  constructor(private fb: FormBuilder,
              private store: PortfolioCardStore,
              private cd: ChangeDetectorRef) {
    this.form = fb.group({
      desiredPositions: fb.array([]),
    });
  }

  get desiredPositions(): FormArray {
    return this.form.get('desiredPositions') as FormArray;
  }

  ngOnInit(): void {
    this.setupFormBeforeData$ = this.data$.pipe(tapOnce(data => {
      //TODO use card.desiredPositionByIssuer if present and fall back to item.quantity if not
      data.items.forEach((item: any) => this.desiredPositions.push(this.fb.control(item.quantity)));
    }));
  }

  ngAfterViewInit(): void {
    this.desiredPositions.valueChanges.pipe(
      untilDestroyed(this),
      debounceTime(300),
      skip(1),
      withLatestFrom(this.data$)
    ).subscribe(([values, data]) => {
      const desiredPositionByIssuer = data.items.reduce((res, item, i) => {
        res[item.name] = values[i];
        return res;
      }, {} as { [key: string]: number });
      this.store.updateCard({...this.card, desiredPositionByIssuer});
    });
  }
}
