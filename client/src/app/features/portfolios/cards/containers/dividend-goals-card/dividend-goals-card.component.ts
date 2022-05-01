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
      <form [formGroup]="form">
        <mat-list [formArrayName]="'desiredYields'">
          <mat-list-item *ngFor="let item of data.items; let i = index;">
            <span>{{item.name}}</span>
            <mat-form-field class="example-full-width">
              <mat-label>Yield</mat-label>
              <span matPrefix>{{item.currentYield + '% /'}}&nbsp;</span>
              <input matInput [formControlName]="i" placeholder="100" autocomplete="false">
              <span matSuffix>%</span>
            </mat-form-field>
            <button mat-button *ngFor="let target of item.targets">{{target}}</button>
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
      desiredYields: fb.array([]),
    });
  }

  get desiredYields(): FormArray {
    return this.form.get('desiredYields') as FormArray;
  }

  ngOnInit(): void {
    this.setupFormBeforeData$ = this.data$.pipe(tapOnce(data => {
      console.log(this.card.desiredYieldByIssuer)
      data.items.forEach((item) =>
        this.desiredYields.push(this.fb.control(this.card.desiredYieldByIssuer?.[item.name] ?? item.currentYield)));
    }));
  }

  ngAfterViewInit(): void {
    this.desiredYields.valueChanges.pipe(
      untilDestroyed(this),
      debounceTime(300),
      skip(1),
      withLatestFrom(this.data$)
    ).subscribe(([values, data]) => {
      const desiredYieldByIssuer = data.items.reduce((res, item, i) => {
        res[item.name] = values[i];
        return res;
      }, {} as { [key: string]: number });
      this.store.updateCard({...this.card, desiredYieldByIssuer});
    });
  }
}
