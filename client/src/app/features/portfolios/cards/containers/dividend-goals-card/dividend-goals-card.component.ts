import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { debounceTime, Observable, skip } from 'rxjs';
import { FormArray, FormBuilder, FormGroup } from '@angular/forms';
import { DividendGoalsCardData } from '../../types/out/dividend-goals-card-data';
import { PortfolioCard } from '../../types/portfolio-card';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-dividend-goals-card',
  template: `
    <ng-container *ngIf="data$ | async as data">
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
export class DividendGoalsCardComponent implements CardContainer<PortfolioCard, DividendGoalsCardData>, OnInit, AfterViewInit {

  card!: PortfolioCard;
  data$!: Observable<DividendGoalsCardData>;

  form: FormGroup;

  constructor(private fb: FormBuilder) {
    this.form = fb.group({
      desiredPositions: fb.array([]),
    });
  }

  tapIntoData(data: DividendGoalsCardData): void {
    data.items.forEach((item: any) => this.desiredPositions.push(this.fb.control(item.quantity)));
  }

  get desiredPositions(): FormArray {
    return this.form.get('desiredPositions') as FormArray;
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.desiredPositions.valueChanges
      .pipe(
        untilDestroyed(this),
        debounceTime(300),
        skip(1),
      )
      .subscribe(values => {
        console.log(values)
      });
  }
}
