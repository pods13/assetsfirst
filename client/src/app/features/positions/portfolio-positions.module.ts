import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {PortfolioPositionsRoutingModule} from './portfolio-positions-routing.module';
import {PortfolioPositionService} from './services/portfolio-position.service';
import {MatLegacyButtonModule as MatButtonModule} from '@angular/material/legacy-button';
import {MatIconModule} from '@angular/material/icon';
import {PositionTagsDialogComponent} from './components/tags-dialog/position-tags-dialog.component';
import {MatLegacyDialogModule as MatDialogModule} from '@angular/material/legacy-dialog';
import {MatLegacyTabsModule as MatTabsModule} from '@angular/material/legacy-tabs';
import {MatLegacyChipsModule as MatChipsModule} from '@angular/material/legacy-chips';
import {MatLegacyFormFieldModule as MatFormFieldModule} from '@angular/material/legacy-form-field';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {MatLegacyTooltipModule as MatTooltipModule} from '@angular/material/legacy-tooltip';
import {MatLegacyProgressSpinnerModule as MatProgressSpinnerModule} from '@angular/material/legacy-progress-spinner';
import {MatBadgeModule} from '@angular/material/badge';
import {MatLegacyAutocompleteModule as MatAutocompleteModule} from '@angular/material/legacy-autocomplete';
import {MatLegacyCardModule as MatCardModule} from '@angular/material/legacy-card';
import {TagCategoriesDialogComponent} from './components/tag-categories-dialog/tag-categories-dialog.component';
import {MatLegacySlideToggleModule as MatSlideToggleModule} from '@angular/material/legacy-slide-toggle';
import {TagCategoryControlComponent} from './components/tag-category-control/tag-category-control.component';
import {MatExpansionModule} from '@angular/material/expansion';
import {MatLegacyInputModule as MatInputModule} from '@angular/material/legacy-input';
import {CoreModule} from '@core/core.module';
import {PositionsContainerV2Component} from './containers/positions-container-v2/positions-container-v2.component';
import {PositionStore} from './services/position.store';
import {MatSortModule} from '@angular/material/sort';
import {PortfolioService} from "./services/portfolio.service";


@NgModule({
    declarations: [
        PositionTagsDialogComponent,
        TagCategoriesDialogComponent,
        TagCategoryControlComponent,
        PositionsContainerV2Component
    ],
    imports: [
        CommonModule,
        PortfolioPositionsRoutingModule,
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
        MatExpansionModule,
        MatInputModule,
        CoreModule,
        MatSortModule,
    ],
    providers: [
        PortfolioPositionService,
        PositionStore,
        PortfolioService
    ]
})
export class PortfolioPositionsModule {
}
