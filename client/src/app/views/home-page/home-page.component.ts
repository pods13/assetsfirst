import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map, shareReplay } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { Router } from '@angular/router';
import { ECharts, EChartsOption } from 'echarts';
import { shortNumber } from '../../core/helpers/number.helpers';

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
          <mat-toolbar [color]="'primary'" class="container">
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
                    <a class="btn btn-start" mat-raised-button color="accent" target="_blank"
                       (click)="onStartNowClick()">
                      Start now
                    </a>
                    <a routerLink="signup" class="btn" mat-button color="accent">
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
                </div>
              </section>
            </div>
          </div>
        </mat-sidenav-content>
      </mat-sidenav-container>
    </ng-container>
    <ng-template #showSpinner>
      <div class="spinner-wrapper">
        <mat-spinner [color]="'accent'"></mat-spinner>
      </div>
    </ng-template>

  `,
  styleUrls: ['./home-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomePageComponent implements OnInit {

  loading = false;
  chartOption!: EChartsOption;

  isHandset$ = this.breakpointObserver.observe([Breakpoints.Handset]).pipe(
    map(result => result.matches),
    shareReplay()
  );

  constructor(private breakpointObserver: BreakpointObserver,
              private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
    this.chartOption = this.constructChartOption();
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
    return {
      title: {
        text: 'Portfolio',
        subtext: '879376$ (+10.49%)',
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
        data: ["2022-06-24", "2022-09-05", "2022-11-17", "2023-01-29", "2023-04-12", "2023-06-24"],
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
          formatter: shortNumber
        }
      },
      series: [
        {
          data: [342987, 380066.684064, 473211.729064, 639344.529064, 706919.129064, 779376.729064],
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
