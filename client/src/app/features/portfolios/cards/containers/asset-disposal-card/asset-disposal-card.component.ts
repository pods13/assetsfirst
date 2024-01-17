import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {CardContainer} from '../../types/card-container';
import {Observable} from 'rxjs';
import {AssetDisposalCard} from '../../types/in/asset-disposal-card';
import {AssetDisposalCardData, AssetDisposalDetails} from '../../types/out/asset-disposal-card-data';
import {ECharts, EChartsOption} from 'echarts';
import {UntilDestroy, untilDestroyed} from '@ngneat/until-destroy';
import {shortNumber} from "@core/helpers/number.helpers";
import {CurrencyPipe} from "@angular/common";
import {stringifyTicker} from "@core/types/ticker";

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
        const tooltipFormatter = (details: AssetDisposalDetails[]) => {
            const disposalDetails = details
                .map(detail => `<span class="disposal-detail">${stringifyTicker(detail.ticker)}: ${this.currencyPipe.transform(detail.total, cardData.currencyCode)}</span>`).join("");
            return `<div class="tooltip-disposal">
                       <div class="tooltip-disposal-details">${disposalDetails}</div>
                    </div>`;
        }
        return {
            tooltip: {
                trigger: 'item',
                position: 'inside',

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
                    tooltip: {
                        valueFormatter: (value) => {
                            if (Array.isArray(value)) {
                                return '' as string;
                            }
                            return this.currencyPipe.transform(value as number, cardData.currencyCode) as string;
                        }
                    },
                    name: 'Taxable Income',
                    type: 'bar',
                    label: {
                        show: cardData.taxableIncome > 0,
                        position: 'inside',
                        formatter: (params: any) => {
                            return this.currencyPipe.transform(params.value, cardData.currencyCode) as any;
                        }
                    },
                    emphasis: {
                        focus: 'series'
                    },
                    data: [cardData.taxableIncome]
                },
                {
                    tooltip: {
                        formatter: (params: any) => {
                            return tooltipFormatter(cardData.profitDetails);
                        }
                    },
                    name: 'Profit',
                    type: 'bar',
                    stack: 'Total',
                    label: {
                        show: cardData.profits > 0,
                        formatter: (params: any) => {
                            return this.currencyPipe.transform(params.value, cardData.currencyCode) as any;
                        }
                    },
                    emphasis: {
                        focus: 'series'
                    },
                    data: [cardData.profits]
                },
                {
                    tooltip: {
                        formatter: (params: any) => {
                            return tooltipFormatter(cardData.lossDetails);
                        }
                    },
                    name: 'Loss',
                    type: 'bar',
                    stack: 'Total',
                    label: {
                        show: cardData.losses < 0,
                        position: 'inside',
                        formatter: (params: any) => {
                            return this.currencyPipe.transform(params.value, cardData.currencyCode) as any;
                        }
                    },
                    emphasis: {
                        focus: 'series'
                    },
                    data: [cardData.losses]
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
