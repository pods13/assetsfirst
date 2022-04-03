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
import { FormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { CardService } from './services/card.service';
import { MatMenuModule } from '@angular/material/menu';
import { MatIconModule } from '@angular/material/icon';
import { PieChartModule } from '@swimlane/ngx-charts';



@NgModule({
  declarations: [
    CardWrapperComponent,
    PortfolioCardOutletDirective,
    AllocationCardComponent,
    SelectCardDialogComponent
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
        PieChartModule
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
export class PortfolioCardsModule { }
