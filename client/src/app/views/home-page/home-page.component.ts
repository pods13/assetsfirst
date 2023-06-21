import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map, shareReplay } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';
import { Router } from '@angular/router';

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
                <svg style="margin-left: 4px;" width="7" height="11" viewBox="0 0 7 11" fill="none" xmlns="http://www.w3.org/2000/svg">
                  <path d="M0.75 10L5.25 5.5L0.75 1" stroke="#1D1D1F" stroke-width="2"></path>
                </svg>
              </a>
            </div>
          </mat-toolbar>
          <div class="wrapper">
            <div class="position-relative">
              <section class="section section-lg section-hero section-shaped">
                <!-- Background circles -->
                <div class="shape shape-style-1 shape-primary">
                  <span class="span-150"></span>
                  <span class="span-50"></span>
                  <span class="span-50"></span>
                  <span class="span-75"></span>
                  <span class="span-100"></span>
                  <span class="span-75"></span>
                  <span class="span-50"></span>
                  <span class="span-100"></span>
                  <span class="span-50"></span>
                  <span class="span-100"></span>
                </div>
                <div class="container shape-container d-flex align-items-center py-lg">
                  <div class="col px-0">
                    <div class="row align-items-center justify-content-center">
                      <div class="col-lg-6 text-center">
                        <!--                    TODO add logo after it will be ready-->
                        <!--                    <img src="./assets/img/brand/argon-white.png" alt="AssetsFirst.ru" style="width: 200px;" class="img-fluid">-->
                        <p class="lead text-white">Make smart decisions with your assets</p>
                        <div class="btn-wrapper mt-5">
                          <a routerLink="signup" class="btn" mat-raised-button color="primary">
                            <span class="btn-inner--text">Sign up</span>
                          </a>
                          <a class="btn btn-try" mat-raised-button target="_blank" (click)="onTryNowClick()">
                            <span>Try now</span>
                          </a>
                        </div>
                      </div>
                    </div>
                  </div>
                </div>
                <!-- SVG separator -->
                <div class="separator separator-bottom separator-skew zindex-100">
                  <svg x="0" y="0" viewBox="0 0 2560 100" preserveAspectRatio="none" version="1.1"
                       xmlns="http://www.w3.org/2000/svg">
                    <polygon class="fill-white" points="2560 0 2560 100 0 100"></polygon>
                  </svg>
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

  isHandset$ = this.breakpointObserver.observe([Breakpoints.Handset]).pipe(
    map(result => result.matches),
    shareReplay()
  );

  constructor(private breakpointObserver: BreakpointObserver,
              private authService: AuthService,
              private router: Router) {
  }

  ngOnInit(): void {
  }

  onTryNowClick() {
    this.loading = true;
    this.authService.signupAsAnonymousUser()
      .subscribe(() => {
        this.loading = false;
        this.router.navigate([this.authService.INITIAL_PATH])
      });
  }

}
