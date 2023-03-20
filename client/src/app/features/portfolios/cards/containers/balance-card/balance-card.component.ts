import { AfterViewInit, ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { BalanceCard } from '../../types/in/balance-card';
import { BalanceCardData } from '../../types/out/balance-card-data';
import { ECharts, EChartsOption } from 'echarts';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-balance-card',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card.title }}</h2>
    </div>
    <ng-container *ngIf="data$ | async as data">
      <div class="current-amount">{{data.currentAmount | currency: data.currencySymbol}}</div>
      <div class="invested-amount">
        <span>{{data.investedAmount | currency: data.currencySymbol}}</span>
        <span>{{'invested'}}</span>
      </div>
    </ng-container>
    <div echarts class="balance-chart" [options]="chartOption" [loading]="loading"
         (chartInit)="onChartInit($event)">
    </div>
  `,
  styleUrls: ['./balance-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class BalanceCardComponent implements OnInit, CardContainer<BalanceCard, BalanceCardData>, AfterViewInit {

  card!: BalanceCard;
  data$!: Observable<BalanceCardData>;

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

  constructChartOption(cardData: BalanceCardData): EChartsOption {
    const {xaxis, values} = cardData.investedAmountByDates;
    return {
      tooltip: {
        trigger: 'item'
      },
      xAxis: {
        type: 'category',
        boundaryGap: false,
        data: xaxis,
        show: this.card.cols > 3
      },
      yAxis: {
        type: 'value',
        show: this.card.cols >= 6 && this.card.rows >= 3
      },
      series: [
        {
          data: values,
          type: 'line',
          areaStyle: {},
          triggerLineEvent: true
        }
      ]
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
