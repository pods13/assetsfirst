import { NgModule } from '@angular/core';
import { CommonModule, CurrencyPipe } from '@angular/common';
import { CardWrapperComponent } from './containers/card-wrapper/card-wrapper.component';
import { PortfolioCardOutletDirective } from './directives/portfolio-card-outlet.directive';
import { AllocationCardComponent } from './containers/allocation-card/allocation-card.component';
import { CardContentLoaderService } from './services/card-content-loader.service';
import { SelectCardDialogComponent } from './components/select-card-dialog/select-card-dialog.component';
import { MatLegacyDialogModule as MatDialogModule } from '@angular/material/legacy-dialog';
import { MatLegacyFormFieldModule as MatFormFieldModule } from '@angular/material/legacy-form-field';
import { MatLegacySelectModule as MatSelectModule } from '@angular/material/legacy-select';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatLegacyButtonModule as MatButtonModule } from '@angular/material/legacy-button';
import { CardService } from './services/card.service';
import { MatLegacyMenuModule as MatMenuModule } from '@angular/material/legacy-menu';
import { MatIconModule } from '@angular/material/icon';
import { DividendIncomeCardComponent } from './containers/dividend-income-card/dividend-income-card.component';
import { FitChartDirective } from './directives/fit-chart.directive';
import { DividendGoalsCardComponent } from './containers/dividend-goals-card/dividend-goals-card.component';
import { MatLegacyListModule as MatListModule } from '@angular/material/legacy-list';
import { MatLegacyInputModule as MatInputModule } from '@angular/material/legacy-input';
import {
  SectoralDistributionCardComponent
} from './containers/sectoral-distribution-card/sectoral-distribution-card.component';
import { BalanceCardComponent } from './containers/balance-card/balance-card.component';
import { ContributionCardComponent } from './containers/contribution-card/contribution-card.component';
import { InvestmentYieldCardComponent } from './containers/investment-yield-card/investment-yield-card.component';
import { NgxEchartsModule } from 'ngx-echarts';
import { EquityRiskPremiumComponent } from './containers/equity-risk-premium/equity-risk-premium.component';
import { MatLegacyTableModule as MatTableModule } from '@angular/material/legacy-table';
import { DragDropModule } from '@angular/cdk/drag-drop';
import { EditAllocationCardComponent } from './components/edit-allocation-card/edit-allocation-card.component';
import { MatExpansionModule } from '@angular/material/expansion';
import { CoreModule } from '@core/core.module';
import { MatLegacyChipsModule as MatChipsModule } from '@angular/material/legacy-chips';
import { MatLegacyAutocompleteModule as MatAutocompleteModule } from '@angular/material/legacy-autocomplete';
import { SegmentControlComponent } from './components/segment-control/segment-control.component';
import { EditDividendIncomeCardComponent } from './components/edit-dividend-income-card/edit-dividend-income-card.component';
import { MatLegacyCheckboxModule as MatCheckboxModule } from '@angular/material/legacy-checkbox';
import { AssetDisposalCardComponent } from './containers/asset-disposal-card/asset-disposal-card.component';


@NgModule({
  declarations: [
    CardWrapperComponent,
    PortfolioCardOutletDirective,
    AllocationCardComponent,
    SelectCardDialogComponent,
    DividendIncomeCardComponent,
    FitChartDirective,
    DividendGoalsCardComponent,
    SectoralDistributionCardComponent,
    BalanceCardComponent,
    ContributionCardComponent,
    InvestmentYieldCardComponent,
    EquityRiskPremiumComponent,
    EditAllocationCardComponent,
    SegmentControlComponent,
    EditDividendIncomeCardComponent,
    AssetDisposalCardComponent,
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
        MatListModule,
        MatInputModule,
        ReactiveFormsModule,
        NgxEchartsModule.forRoot({
            echarts: () => import('echarts')
        }),
        MatTableModule,
        DragDropModule,
        MatExpansionModule,
        CoreModule,
        MatChipsModule,
        MatAutocompleteModule,
        MatCheckboxModule,
    ],
  providers: [
    CardContentLoaderService,
    CardService,
    CurrencyPipe
  ],
  exports: [
    CardWrapperComponent,
    PortfolioCardOutletDirective,
    SelectCardDialogComponent
  ]
})
export class DashboardCardsModule {
}
