import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { CardWrapperComponent } from './containers/card-wrapper/card-wrapper.component';
import { PortfolioCardOutletDirective } from './directives/portfolio-card-outlet.directive';
import { AllocationCardComponent } from './containers/allocation-card/allocation-card.component';
import { CardContentLoaderService } from './services/card-content-loader.service';
import { SelectCardDialogComponent } from './components/select-card-dialog/select-card-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatSelectModule } from '@angular/material/select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { CardService } from './services/card.service';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { BarChartModule, PieChartModule } from '@swimlane/ngx-charts';
import { DividendsCardComponent } from './containers/dividends-card/dividends-card.component';
import { FitChartDirective } from './directives/fit-chart.directive';
import { DividendGoalsCardComponent } from './containers/dividend-goals-card/dividend-goals-card.component';
import { MatListModule } from '@angular/material/list';
import { MatInputModule } from '@angular/material/input';


@NgModule({
  declarations: [
    CardWrapperComponent,
    PortfolioCardOutletDirective,
    AllocationCardComponent,
    SelectCardDialogComponent,
    DividendsCardComponent,
    FitChartDirective,
    DividendGoalsCardComponent,
  ],
  imports: [
    CommonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatSelectModule,
    FormsModule,
    MatButtonModule,
    MatMenuModule,
    MatIconModule,
    PieChartModule,
    BarChartModule,
    MatListModule,
    MatInputModule,
    ReactiveFormsModule
  ],
  providers: [
    CardContentLoaderService,
    CardService
  ],
  exports: [
    CardWrapperComponent,
    PortfolioCardOutletDirective,
    SelectCardDialogComponent
  ]
})
export class PortfolioCardsModule {
}
