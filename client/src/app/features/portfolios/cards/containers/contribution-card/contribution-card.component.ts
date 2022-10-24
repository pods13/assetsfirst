import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { ContributionCardData } from '../../types/out/contribution-card-data';
import { Observable } from 'rxjs';
import { ContributionCard } from '../../types/in/contribution-card';

@Component({
  selector: 'app-contribution-card',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card?.title }}</h2>
    </div>
    <ng-container *ngIf="data$ | async as data">
      <ngx-charts-bar-vertical-stacked
        [view]="[card.cols * 100, card.rows * 100 - 5]"
        [scheme]="'vivid'"
        [results]="data.contributions"
        [xAxis]="true"
        [yAxis]="true"
        [showXAxisLabel]="true"
        [xAxisLabel]="'Months of contributions'"
        [animations]="true">
      </ngx-charts-bar-vertical-stacked>
    </ng-container>
  `,
  styleUrls: ['./contribution-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContributionCardComponent implements CardContainer<ContributionCard, ContributionCardData>, OnInit {

  card!: ContributionCard;
  data$!: Observable<ContributionCardData>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
