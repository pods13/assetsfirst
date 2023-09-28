import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { ContributionCardData } from '../../types/out/contribution-card-data';
import { Observable } from 'rxjs';
import { ContributionCard } from '../../types/in/contribution-card';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { BarSeriesOption, ECharts, EChartsOption } from 'echarts';
import { shortNumber } from '@core/helpers/number.helpers';

@UntilDestroy()
@Component({
  selector: 'app-contribution-card',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card.title }}</h2>
    </div>
    <ng-container *ngIf="data$ | async as data">
      <div class="contributed-amount">
        <span>{{data.totalContributed | currency: data.currencyCode}}</span>
        <span>{{'contributed this year'}}</span>
      </div>
    </ng-container>
    <div echarts class="contribution-chart" [options]="chartOption" [loading]="loading"
         (chartInit)="onChartInit($event)">
    </div>
  `,
  styleUrls: ['./contribution-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class ContributionCardComponent implements CardContainer<ContributionCard, ContributionCardData>, OnInit, AfterViewInit {

  card!: ContributionCard;
  data$!: Observable<ContributionCardData>;

  chartOption!: EChartsOption;
  echartsInstance!: ECharts;
  loading: boolean = false;

  constructor(private cd: ChangeDetectorRef) {
  }

  ngOnInit(): void {
  }

  ngAfterViewInit(): void {
    this.data$.pipe(untilDestroyed(this))
      .subscribe(data => {
        this.chartOption = this.constructChartOption(data);
        this.loading = false;
        this.cd.detectChanges();
      });
  }

  constructChartOption(cardData: ContributionCardData): EChartsOption {
    const series: BarSeriesOption[] = cardData.contributions.map(c => ({
      name: c.name,
      stack: 'one',
      data: c.data,
      type: 'bar'
    }));
    return {
      tooltip: {
        trigger: 'item'
      },
      xAxis: {
        name: 'Months of contributions',
        nameLocation: 'middle',
        nameTextStyle: {
          fontSize: 16,
          padding: 12
        },
        data: cardData.xaxis,
        axisLine: {onZero: true},
        splitLine: {show: true},
        splitArea: {show: true}
      },
      yAxis: {
        axisLabel: {
          formatter: function (value: any) {
            return shortNumber(value);
          }
        }
      },
      series
    };
  }

  onChartInit(ec: ECharts) {
    this.echartsInstance = ec;
    ec.resize({
      width: this.card.cols * 110,
      height: this.card.rows * 100
    });
  }

}
