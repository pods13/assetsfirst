import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { BreakpointObserver, Breakpoints } from '@angular/cdk/layout';
import { map, shareReplay } from 'rxjs';
import { AuthService } from '../../auth/services/auth.service';

@Component({
  selector: 'app-home-page',
  template: `
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
        <mat-toolbar [color]="'primary'">
          <button type="button" aria-label="Toggle sidenav" mat-icon-button
                  (click)="sidenav.toggle()" *ngIf="isHandset$ | async">
            <mat-icon aria-label="Side nav toggle icon">menu</mat-icon>
          </button>
          <span class="logo" routerLink="">Assets</span>
          <span class="fill-space"></span>
          <div *ngIf="!(isHandset$ | async)" class="top-nav">
            <a routerLink="login" mat-button>Log In</a>
            <a routerLink="signup" mat-button>Sign Up</a>
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
                      <a routerLink="signup"  class="btn" mat-raised-button color="primary">
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
              <svg x="0" y="0" viewBox="0 0 2560 100" preserveAspectRatio="none" version="1.1" xmlns="http://www.w3.org/2000/svg">
                <polygon class="fill-white" points="2560 0 2560 100 0 100"></polygon>
              </svg>
            </div>
          </section>
        </div>
        </div>
      </mat-sidenav-content>
    </mat-sidenav-container>

  `,
  styleUrls: ['./home-page.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HomePageComponent implements OnInit {

  isHandset$ = this.breakpointObserver.observe([Breakpoints.Handset]).pipe(
    map(result => result.matches),
    shareReplay()
  );

  constructor(private breakpointObserver: BreakpointObserver) { }

  ngOnInit(): void {
  }

  onTryNowClick() {
    //TODO check if user already login if not create new user with dummy portfolio
  }

}
