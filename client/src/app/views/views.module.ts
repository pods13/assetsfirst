import { NgModule } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { HomePageComponent } from './home-page/home-page.component';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatLegacyListModule as MatListModule } from '@angular/material/legacy-list';
import { MatIconModule } from '@angular/material/icon';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { RouterModule } from '@angular/router';
import { MatLegacyProgressSpinnerModule as MatProgressSpinnerModule } from '@angular/material/legacy-progress-spinner';
import { NgxEchartsModule } from 'ngx-echarts';
import { MatLegacyCardModule as MatCardModule } from '@angular/material/legacy-card';


@NgModule({
  declarations: [
    HomePageComponent
  ],
  imports: [
    CommonModule,
    MatSidenavModule,
    MatToolbarModule,
    MatListModule,
    MatIconModule,
    MatButtonModule,
    RouterModule,
    MatProgressSpinnerModule,
    NgxEchartsModule.forRoot({
      echarts: () => import('echarts')
    }),
    MatCardModule,
  ],
  providers: [
    CurrencyPipe
  ]
})
export class ViewsModule {
}
