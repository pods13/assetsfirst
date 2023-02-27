import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { PortfolioPositionsRoutingModule } from './portfolio-positions-routing.module';
import { PositionsContainerComponent } from './containers/positions-container/positions-container.component';
import { PortfolioPositionService } from './services/portfolio-position.service';
import { NgxDatatableModule } from '@swimlane/ngx-datatable';
import { MatButtonModule } from '@angular/material/button';
import { MatIconModule } from '@angular/material/icon';
import { PositionTagsDialogComponent } from './components/tags-dialog/position-tags-dialog.component';
import { MatDialogModule } from '@angular/material/dialog';
import { MatTabsModule } from '@angular/material/tabs';
import { TagCategoryService } from './services/tag-category.service';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { ReactiveFormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';


@NgModule({
  declarations: [
    PositionsContainerComponent,
    PositionTagsDialogComponent
  ],
    imports: [
        CommonModule,
        PortfolioPositionsRoutingModule,
        NgxDatatableModule,
        MatButtonModule,
        MatIconModule,
        MatDialogModule,
        MatTabsModule,
        MatChipsModule,
        MatFormFieldModule,
        ReactiveFormsModule,
        MatTooltipModule,
    ],
  providers: [
    PortfolioPositionService,
    TagCategoryService
  ]
})
export class PortfolioPositionsModule {
}
