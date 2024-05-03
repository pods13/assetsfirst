import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  EventEmitter,
  OnInit,
  ViewEncapsulation
} from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { DividendIncomeCardData, TimeFrameDividend } from '../../types/out/dividend-income-card-data';
import { DividendIncomeCard, TimeFrameOption } from '../../types/in/dividend-income-card';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { ECharts, EChartsOption } from 'echarts';
import { shortNumber } from '@core/helpers/number.helpers';
import { CurrencyPipe } from '@angular/common';

@UntilDestroy()
@Component({
  selector: 'app-dividends-card',
  template: `
      <div class="card-header">
          <h2 class="title">{{ card.title }}</h2>
          <mat-select class="group-by-selector" [value]="card.timeFrame" (valueChange)="onTimeFrameOptionChanged($event)" hideSingleSelectionIndicator>
              <mat-option *ngFor="let timeFrame of timeFrameOptions"
                          [value]="timeFrame.value">
                  {{ timeFrame.value }}
              </mat-option>
          </mat-select>
      </div>
      <div echarts class="dividend-income-chart" [options]="chartOption" [loading]="loading"
           (chartInit)="onChartInit($event)">
      </div>
  `,
  styleUrls: ['./dividend-income-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
  encapsulation: ViewEncapsulation.None
})
export class DividendIncomeCardComponent implements OnInit, AfterViewInit, CardContainer<DividendIncomeCard, DividendIncomeCardData> {

  card!: DividendIncomeCard;
  data$!: Observable<DividendIncomeCardData>;

  cardChanges$ = new EventEmitter<DividendIncomeCard>();

  timeFrameOptions = Object.keys(TimeFrameOption).map(timeFrame => ({value: timeFrame}));

  chartOption!: EChartsOption;
  echartsInstance!: ECharts;
  loading: boolean = false;

  constructor(private cd: ChangeDetectorRef,
              private currencyPipe: CurrencyPipe) {
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

  constructChartOption(cardData: DividendIncomeCardData): EChartsOption {
    const dividendsData = cardData.dividends;
    const xAxis = dividendsData.map(d => d.name);
    return {
      tooltip: {
        trigger: 'item',
        position: 'inside',
        formatter: (params: any) => {
          const timeFrameDividend = dividendsData.find(d => d.name === params.name);
          if (!timeFrameDividend) {
            return `<div class="tooltip-dividend">`
              + `<div class="tooltip-dividend-title">${params.seriesName}: ${params.value}</div>`
              + `</div>`;
          }
          const details = timeFrameDividend.series[params.seriesIndex].details;
          const mergedDetails = Array.from(details.reduce(
            (acc, item) => (item.name && acc.set(item.name, (acc.get(item.name) || 0) + item.total), acc),
            new Map()
          ), ([name, total]) => ({name, total}));
          const detailsByCurrencies = details.reduce((acc, item) => (item.name && acc.set(item.name, item.currency), acc), new Map());
          const dividendDetails = mergedDetails
            .map(detail => `<span class="dividend-detail">${detail.name}: ${this.currencyPipe.transform(detail.total, detailsByCurrencies.get(detail.name))}</span>`).join("");
          const portfolioCurrency = timeFrameDividend.series[params.seriesIndex].currencyCode;
          return `<div class="tooltip-dividend">`
            + `<div class="tooltip-dividend-title">${params.seriesName}: ${this.currencyPipe.transform(params.value, portfolioCurrency)}</div>`
            + `<div class="tooltip-dividend-details">${dividendDetails}</div>`
            + `</div>`;
        }
      },
      xAxis: {
        type: 'category',
        data: xAxis
      },
      yAxis: {
        type: 'value',
        axisLabel: {
          formatter: function (value: any) {
            return shortNumber(value);
          }
        }
      },
      series: this.composeBarSeries(dividendsData)
    };
  }

  composeBarSeries(dividendsData: TimeFrameDividend[]) {
    const series: any[] = [];
    dividendsData.forEach((d, i) => {
      if (i === 0) {
        d.series.forEach(s => series.push({
          name: s.name,
          stack: s.stack,
          data: [s.value],
          type: 'bar',
          showBackground: true,
          details: [s.details]
        }))
      } else {
        d.series.forEach((s, j) => {
          series[j].data.push(s.value)
          series[j].details.push(s.details)
        });
      }
    });
    return series;
  }

  onChartInit(ec: ECharts) {
    this.echartsInstance = ec;
    ec.resize({
      width: this.card.cols * 110,
      height: this.card.rows * 100
    });
  }


  onTimeFrameOptionChanged(timeFrame: TimeFrameOption) {
    this.loading = true;
    this.cd.detectChanges();
    this.cardChanges$.emit({...this.card, timeFrame});
  }
}
