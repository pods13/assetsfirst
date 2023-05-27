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
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatTooltipModule } from '@angular/material/tooltip';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatBadgeModule } from '@angular/material/badge';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCardModule } from '@angular/material/card';
import { TagCategoriesDialogComponent } from './components/tag-categories-dialog/tag-categories-dialog.component';
import { MatSlideToggleModule } from '@angular/material/slide-toggle';


@NgModule({
  declarations: [
    PositionsContainerComponent,
    PositionTagsDialogComponent,
    TagCategoriesDialogComponent
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
    MatProgressSpinnerModule,
    MatBadgeModule,
    MatAutocompleteModule,
    MatCardModule,
    MatSlideToggleModule,
    FormsModule,
  ],
  providers: [
    PortfolioPositionService,
    TagCategoryService
  ]
})
export class PortfolioPositionsModule {
}
