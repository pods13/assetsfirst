import {NgModule} from '@angular/core';
import {CommonModule, CurrencyPipe} from '@angular/common';
import {CardWrapperComponent} from './containers/card-wrapper/card-wrapper.component';
import {PortfolioCardOutletDirective} from './directives/portfolio-card-outlet.directive';
import {AllocationCardComponent} from './containers/allocation-card/allocation-card.component';
import {CardContentLoaderService} from './services/card-content-loader.service';
import {SelectCardDialogComponent} from './components/select-card-dialog/select-card-dialog.component';
import {MatDialogModule} from '@angular/material/dialog';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatSelectModule} from '@angular/material/select';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatButtonModule} from '@angular/material/button';
import {CardService} from './services/card.service';
import {MatMenuModule} from '@angular/material/menu';
import {MatIconModule} from '@angular/material/icon';
import {DividendIncomeCardComponent} from './containers/dividend-income-card/dividend-income-card.component';
import {FitChartDirective} from './directives/fit-chart.directive';
import {DividendGoalsCardComponent} from './containers/dividend-goals-card/dividend-goals-card.component';
import {MatListModule} from '@angular/material/list';
import {MatInputModule} from '@angular/material/input';
import {SectoralDistributionCardComponent} from './containers/sectoral-distribution-card/sectoral-distribution-card.component';
import {BalanceCardComponent} from './containers/balance-card/balance-card.component';
import {ContributionCardComponent} from './containers/contribution-card/contribution-card.component';
import {InvestmentYieldCardComponent} from './containers/investment-yield-card/investment-yield-card.component';
import {EquityRiskPremiumComponent} from './containers/equity-risk-premium/equity-risk-premium.component';
import {MatTableModule} from '@angular/material/table';
import {DragDropModule} from '@angular/cdk/drag-drop';
import {EditAllocationCardComponent} from './components/edit-allocation-card/edit-allocation-card.component';
import {MatExpansionModule} from '@angular/material/expansion';
import {CoreModule} from '@core/core.module';
import {MatChipsModule} from '@angular/material/chips';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {SegmentControlComponent} from './components/segment-control/segment-control.component';
import {EditDividendIncomeCardComponent} from './components/edit-dividend-income-card/edit-dividend-income-card.component';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {AssetDisposalCardComponent} from './containers/asset-disposal-card/asset-disposal-card.component';
import {NgxEchartsModule} from "ngx-echarts";


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
        MatTableModule,
        DragDropModule,
        MatExpansionModule,
        CoreModule,
        MatChipsModule,
        MatAutocompleteModule,
        MatCheckboxModule,
        NgxEchartsModule.forRoot({
            echarts: () => import('echarts')
        }),
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
