import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {CardContainer} from '../../types/card-container';
import {Observable} from 'rxjs';
import {AssetDisposalCard} from '../../types/in/asset-disposal-card';
import {AssetDisposalCardData} from '../../types/out/asset-disposal-card-data';
import {ECharts, EChartsOption} from 'echarts';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {shortNumber} from "@core/helpers/number.helpers";
import {CurrencyPipe} from "@angular/common";

@UntilDestroy()
@Component({
    selector: 'app-asset-disposal-card',
    template: `
        <div class="card-header">
            <h2 class="title">{{ card.title }}</h2>
        </div>
        <div echarts class="disposal-chart" [options]="chartOption" [loading]="loading"
             (chartInit)="onChartInit($event)">
        </div>
    `,
    styleUrls: ['./asset-disposal-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class AssetDisposalCardComponent implements OnInit, CardContainer<AssetDisposalCard, AssetDisposalCardData> {

    card!: AssetDisposalCard;
    data$!: Observable<AssetDisposalCardData>;

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

    constructChartOption(cardData: AssetDisposalCardData): EChartsOption {
        return {
            tooltip: {
                trigger: 'item'
            },
            xAxis: [
                {
                    type: 'value',
                    axisLabel: {
                        formatter: function (value: any) {
                            return shortNumber(value);
                        }
                    }
                }
            ],
            yAxis: [
                {
                    type: 'category',
                    axisTick: {
                        show: false
                    },
                    data: cardData.trackedYears
                }
            ],
            series: [
                {
                    name: 'Taxable Income',
                    type: 'bar',
                    label: {
                        show: true,
                        position: 'inside',
                        formatter: (params: any) => {
                            return this.currencyPipe.transform(params.value, cardData.currencyCode) as any;
                        }
                    },
                    emphasis: {
                        focus: 'series'
                    },
                    data: cardData.taxableIncome
                },
                {
                    name: 'Profit',
                    type: 'bar',
                    stack: 'Total',
                    label: {
                        show: true,
                        formatter: (params: any) => {
                            return this.currencyPipe.transform(params.value, cardData.currencyCode) as any;
                        }
                    },
                    emphasis: {
                        focus: 'series'
                    },
                    data: cardData.profits
                },
                {
                    name: 'Loss',
                    type: 'bar',
                    stack: 'Total',
                    label: {
                        show: true,
                        position: 'inside',
                        formatter: (params: any) => {
                            return this.currencyPipe.transform(params.value, cardData.currencyCode) as any;
                        }
                    },
                    emphasis: {
                        focus: 'series'
                    },
                    data: cardData.losses
                }]
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
