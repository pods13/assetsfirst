import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {BreakpointObserver, Breakpoints} from '@angular/cdk/layout';
import {map, shareReplay} from 'rxjs';
import {AuthService} from '../../auth/services/auth.service';
import {Router} from '@angular/router';
import type {ECharts, EChartsOption} from 'echarts';
import {shortNumber} from '@core/helpers/number.helpers';
import {HttpClient} from '@angular/common/http';
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
                        <a mat-list-item routerLink="login" (click)="sidenav.close()">Войти</a>
                        <a mat-list-item routerLink="signup" (click)="sidenav.close()">Регистрация</a>
                    </mat-nav-list>
                </mat-sidenav>
                <mat-sidenav-content>
                    <mat-toolbar>
                        <div class="container">
                            <button type="button" aria-label="Toggle sidenav" mat-icon-button
                                    (click)="sidenav.toggle()" *ngIf="isHandset$ | async">
                                <mat-icon aria-label="Side nav toggle icon">menu</mat-icon>
                            </button>
                            <div class="navbar-brand logo d-flex flex-wrap align-content-center" routerLink=""><span
                                class="assets">assets</span>first
                            </div>
                            <div *ngIf="!(isHandset$ | async)" class="top-nav">
                                <a routerLink="login" mat-button>Войти
                                    <svg style="margin-left: 4px;" width="7" height="11" viewBox="0 0 7 11" fill="none"
                                         xmlns="http://www.w3.org/2000/svg">
                                        <path d="M0.75 10L5.25 5.5L0.75 1" stroke="#1D1D1F" stroke-width="2"></path>
                                    </svg>
                                </a>
                            </div>
                        </div>
                    </mat-toolbar>
                    <div class="wrapper">
                        <div class="container">
                            <section class="row intro">
                                <div class="col col-lg-5 col-12 description">
                                    <div>
                                        <h1>Управляй активами эффективно</h1>
                                        <h2>Используй наш удобный сервис для учета активов или создай собственное решение с помощью нашего
                                            API корпоративных событий.</h2>
                                        <a class="btn btn-start" mat-flat-button color="primary" target="_blank"
                                           (click)="onStartNowClick()">
                                            Попробовать бесплатно
                                        </a>
                                    </div>
                                </div>
                                <div class="col portfolio">
                                    <!--                                    <div echarts class="portfolio-chart" [options]="chartOption" (chartInit)="onChartInit($event)">-->
                                    <!--                                    </div>-->
                                    <div class="portfolio-chart">
                                        <img src="./assets/capital.png" class="img-fluid" alt="График активов">
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
        this.chartOption = this.constructChartOption();
        this.cd.detectChanges();
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

    constructChartOption(): EChartsOption {
        const latestValue = 3582240.00;
        const formattedLatestValue = this.currencyPipe.transform(latestValue, "RUB");
        return {
            title: {
                text: 'Капитал',
                subtext: `${formattedLatestValue} (38.03%)`,
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
                data: [
                    "2023-05-15",
                    "2023-06-09",
                    "2023-07-04",
                    "2023-07-29",
                    "2023-08-23",
                    "2023-09-17",
                    "2023-10-12",
                    "2023-11-06",
                    "2023-12-01",
                    "2023-12-26",
                    "2024-01-20",
                    "2024-02-14",
                    "2024-03-10",
                    "2024-04-04",
                    "2024-04-29",
                    "2024-05-15"
                ],
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
                    data: [
                        2459150.00000,
                        2802195.00000,
                        2892695.00000,
                        3104110.00000,
                        3191400.00000,
                        3272395.00000,
                        3240850.00000,
                        2459150.00000,
                        3347090.00000,
                        3336060.00000,
                        3353020.00000,
                        2459150.00000,
                        3422065.00000,
                        3526560.00000,
                        3600415.00000,
                        3582240.00000
                    ],
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
