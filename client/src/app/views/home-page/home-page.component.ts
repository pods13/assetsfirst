import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {map, shareReplay} from 'rxjs';
import {AuthService} from '../../auth/services/auth.service';
import {Router} from '@angular/router';
import type {ECharts, EChartsOption} from 'echarts';
import {shortNumber} from '@core/helpers/number.helpers';
import {HttpClient} from '@angular/common/http';
import {PortfolioInfoDto} from '@core/types/portfolioInfoDto';
import {PortfolioDividendDto} from '@core/types/portfolio-dividend.dto';
import {Page} from '@core/types/page';
import {CurrencyPipe} from '@angular/common';

@Component({
    selector: 'app-home-page',
    template: `
        <ng-container *ngIf="!loading; else showSpinner">
            <mat-sidenav-container class="sidenav-container">
                <mat-sidenav #sidenav class="sidenav" fixedInViewport
                             [attr.role]="(isHandset$ | async) ? 'dialog' : 'navigation'"
                             [mode]="(isHandset$ | async) ? 'over': 'side'"
                             [opened]="false">
                    <mat-toolbar>Menu</mat-toolbar>
                    <mat-nav-list>
                        <a mat-list-item routerLink="login" (click)="sidenav.close()">Log In</a>
                        <a mat-list-item routerLink="signup" (click)="sidenav.close()">Sign Up</a>
                    </mat-nav-list>
                </mat-sidenav>
                <mat-sidenav-content>
                    <mat-toolbar class="container">
                        <button type="button" aria-label="Toggle sidenav" mat-icon-button
                                (click)="sidenav.toggle()" *ngIf="isHandset$ | async">
                            <mat-icon aria-label="Side nav toggle icon">menu</mat-icon>
                        </button>
                        <div class="logo" routerLink=""><span class="assets">assets</span>first</div>
                        <div></div>
                        <div *ngIf="!(isHandset$ | async)" class="top-nav">
                            <a routerLink="login" mat-button>Sign In
                                <svg style="margin-left: 4px;" width="7" height="11" viewBox="0 0 7 11" fill="none"
                                     xmlns="http://www.w3.org/2000/svg">
                                    <path d="M0.75 10L5.25 5.5L0.75 1" stroke="#1D1D1F" stroke-width="2"></path>
                                </svg>
                            </a>
                        </div>
                    </mat-toolbar>
                    <div class="wrapper">
                        <div class="container">
                            <section class="row intro">
                                <div class="col col-12 col-lg-5 description">
                                    <div>
                                        <h1>Make smart decisions with your assets</h1>
                                        <h2>Build a safer stream of income and preserve your assets with our easy-to-use portfolio
                                            tools.</h2>
                                        <a class="btn btn-start" mat-raised-button color="primary" target="_blank"
                                           (click)="onStartNowClick()">
                                            Start now
                                        </a>
                                        <a routerLink="signup" class="btn" mat-button color="primary">
                                            Sign up
                                            <svg style="margin-left: 4px;" width="7" height="11" viewBox="0 0 7 11" fill="none"
                                                 xmlns="http://www.w3.org/2000/svg">
                                                <path d="M0.75 10L5.25 5.5L0.75 1" stroke="#1D1D1F" stroke-width="2"></path>
                                            </svg>
                                        </a>
                                    </div>
                                </div>
                                <div class="col portfolio">
                                    <div echarts class="portfolio-chart" [options]="chartOption" (chartInit)="onChartInit($event)">
                                    </div>
                                    <mat-card appearance="outlined" class="portfolio-dividends">
                                        <mat-card-content class="container">
                                            <div class="row" *ngFor="let div of dividends$ | async">
                                                <div class="col">
                                                    <h3 class="company-name">{{ div.name }}</h3>
                                                    <p class="pay-date">{{ div.recordDate | date: 'mediumDate' }}</p>
                                                </div>
                                                <div class="col">
                                                    <p class="div-ps">{{ div.perShare | currency: div.currency }}</p>
                                                    <p class="div-total">+{{ div.total | currency: div.currency }}</p>
                                                </div>
                                            </div>
                                        </mat-card-content>
                                    </mat-card>
                                </div>
                            </section>
                        </div>
                    </div>
                </mat-sidenav-content>
            </mat-sidenav-container>
        </ng-container>
        <ng-template #showSpinner>
            <div class="spinner-wrapper">
                <mat-spinner [color]="'primary'"></mat-spinner>
            </div>
        </ng-template>

    `,
    styleUrls: ['./home-page.component.scss'],
    providers: [
        CurrencyPipe
    ],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomePageComponent implements OnInit {

    loading = false;
    chartOption!: EChartsOption;

    isHandset$ = this.breakpointObserver.observe([Breakpoints.Handset]).pipe(
        map(result => result.matches),
        shareReplay()
    );

    dividends$ = this.getPortfolioUpcomingDividends();

    constructor(private breakpointObserver: BreakpointObserver,
                private authService: AuthService,
                private router: Router,
                private http: HttpClient,
                private cd: ChangeDetectorRef,
                private currencyPipe: CurrencyPipe) {
    }

    ngOnInit(): void {
        this.getPortfolioInfo()
            .subscribe((portfolioInfo) => {
                this.chartOption = this.constructChartOption(portfolioInfo);
                this.cd.detectChanges();
            });
    }

    getPortfolioInfo() {
        return this.http.get<PortfolioInfoDto>('/public/portfolios/demo');
    }

    getPortfolioUpcomingDividends() {
        return this.http.get<Page<PortfolioDividendDto[]>>('/public/portfolios/demo/dividends?page=0&size=8&sort=recordDate')
            .pipe(map(page => page.content));
    }

    onStartNowClick() {
        this.loading = true;
        this.authService.signupAsAnonymousUser()
            .subscribe(() => {
                this.loading = false;
                this.router.navigate([this.authService.INITIAL_PATH])
            });
    }

    constructChartOption(dto: PortfolioInfoDto): EChartsOption {
        const marketValueByDates = dto.marketValueByDates;
        const latestValue = marketValueByDates.values[marketValueByDates.values.length - 1];
        const formattedLatestValue = this.currencyPipe.transform(latestValue, dto.currencyCode);
        return {
            title: {
                text: 'Portfolio',
                subtext: `${formattedLatestValue} (${dto.valueIncreasePct}%)`,
                left: '10%',
                top: '10%'
            },
            backgroundColor: 'white',
            tooltip: {
                trigger: 'item'
            },
            xAxis: {
                type: 'category',
                boundaryGap: false,
                data: marketValueByDates.dates,
                show: true,
                axisLine: {
                    show: false,
                    onZero: false,
                },
                axisTick: {
                    show: false
                }
            },
            yAxis: {
                type: 'value',
                show: true,
                position: 'right',
                min: 200000,
                splitLine: {
                    show: false
                },
                axisLabel: {
                    formatter: function (value: any) {
                        return shortNumber(value);
                    }
                }
            },
            series: [
                {
                    data: marketValueByDates.values,
                    type: 'line',
                    areaStyle: {
                        opacity: 0.1
                    },
                    triggerLineEvent: true,
                }
            ],
            grid: {
                show: false
            }
        };
    }

    onChartInit(ec: ECharts) {
        ec.resize({
            width: 523,
            height: 470
        });
    }

}
