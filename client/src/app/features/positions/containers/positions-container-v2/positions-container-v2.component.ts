import {ChangeDetectionStrategy, Component, OnInit} from '@angular/core';
import {PortfolioPositionService} from '../../services/portfolio-position.service';
import {PortfolioPositionView} from '../../types/portfolio-position.view';
import {
    PositionTagsDialogComponent,
    PositionTagsDialogData,
    PositionTagsDialogReturnType
} from '../../components/tags-dialog/position-tags-dialog.component';
import {stringifyTicker} from '@core/types/ticker';
import {
    TagCategoriesDialogComponent,
    TagCategoriesDialogReturnType
} from '../../components/tag-categories-dialog/tag-categories-dialog.component';
import {first, forkJoin} from 'rxjs';
import {TagCategoryService} from '@core/services/tag-category.service';
import {MatLegacyDialog as MatDialog} from '@angular/material/legacy-dialog';
import {PositionStore} from '../../services/position.store';
import {Sort} from '@angular/material/sort';
import {PortfolioService} from "../../services/portfolio.service";

@Component({
    selector: 'app-positions-container-v2',
    template: `
        <div class="positions-list" matSort (matSortChange)="sortPositions($event)"
             [matSortActive]="defaultSort.active" [matSortDirection]="defaultSort.direction">
            <div class="portfolio-header-box row" >
                <div class="column">
                    <div class="total-investment column" *ngIf="portfolioShortInfo | async as portfolioInfo">
                        <div class="row header">Total investment</div>
                        <div class="row value">{{portfolioInfo.investedValue | currency: portfolioInfo.currencyCode}}</div>
                    </div>
                </div>
                <div class="column positions-action-list">
                    <button mat-button>
                        <mat-icon>table_chart</mat-icon>
                    </button>
                </div>

            </div>
            <div class="position-header-box row">
                <ng-container *ngFor="let column of displayedColumns">
                    <div *ngIf="column.sortable; else notSortable" class="column" [mat-sort-header]="column.id">
                        {{column.name}}
                    </div>
                    <ng-template #notSortable>
                        <div class="column">{{column.name}}</div>
                    </ng-template>
                </ng-container>
            </div>
            <ng-container *ngIf="store.state$ | async as state">
                <div class="position-box row" *ngFor="let position of state.positions">
                    <div class="column">
                        <a class="identifier-symbol"
                           [routerLink]="'/symbols/' + position.identifier.exchange + '-' + position.identifier.symbol">
                            {{position.identifier.symbol}}
                        </a>
                        <div class="identifier-company">{{position.companyName}}</div>
                    </div>
                    <div class="column">{{position.pctOfPortfolio + '%'}}</div>
                    <div class="column">{{position.price | currency: position.currencyCode}}</div>
                    <div class="column">{{position.yieldOnCost + '%'}}</div>
                    <div class="column">{{position.accumulatedDividends | currency: position.currencyCode}}</div>
                    <div class="column">{{position.upcomingDividendDate}}</div>
                    <div class="column" *ngIf="position.tags.length === 0; else tags">
                        <button mat-button (click)="openPositionTagsDialog($event, position)">
                            <mat-icon>library_add</mat-icon>
                        </button>
                    </div>
                    <ng-template #tags>
                        <div class="column">
                            <button mat-button (click)="openPositionTagsDialog($event, position)">
                                <mat-icon [matBadge]="position.tags.length" matBadgeColor="accent">tag</mat-icon>
                                <span class="cdk-visually-hidden">{{position.tags.length + ' tags linked to this position'}}</span>
                            </button>
                        </div>
                    </ng-template>
                </div>
            </ng-container>
        </div>
    `,
    styleUrls: ['./positions-container-v2.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class PositionsContainerV2Component implements OnInit {

    defaultSort: Sort = {active: 'pctOfPortfolio', direction: 'desc'};

    displayedColumns = [
        {name: 'Ticker', sortable: false, id: 'ticker'},
        {name: '% Of Portfolio', sortable: true, id: 'pctOfPortfolio'},
        {name: 'Cost Per Share', sortable: false, id: 'price'},
        {name: 'Yield on Cost', sortable: true, id: 'yieldOnCost'},
        {name: 'Accumulated Dividends', sortable: false, id: 'accumulatedDividends'},
        {name: 'Upcoming Dividend Date', sortable: false, id: 'upcomingDividendDate'},
        {name: 'Tags', sortable: false, id: 'tags'},
    ];
    portfolioShortInfo = this.portfolioService.getPortfolioShortInfo();

    constructor(private portfolioPositionService: PortfolioPositionService,
                private tagCategoryService: TagCategoryService,
                private matDialog: MatDialog,
                public store: PositionStore,
                private portfolioService: PortfolioService) {
    }

    ngOnInit(): void {
        this.initPositionsStore();
    }

    initPositionsStore() {
        this.portfolioPositionService.getPortfolioPositionsView(true).pipe(
            first(),
        ).subscribe(positions => this.store.init(positions, this.defaultSort));
    }

    getPortfolioPositions() {
        return this.portfolioPositionService.getPortfolioPositionsView(true);
    }

    openPositionTagsDialog(event: MouseEvent, position: PortfolioPositionView) {
        const dialogRef = this.matDialog.open<any, PositionTagsDialogData, PositionTagsDialogReturnType>(PositionTagsDialogComponent, {
            restoreFocus: false,
            disableClose: false,
            data: {
                title: `${stringifyTicker(position.identifier)} Position Tags`,
                selectedTags: position.tags
            }
        });
        dialogRef.afterClosed().subscribe((value) => {
            if (!value) {
                return;
            }
            if (value.openTagCategoriesDialog) {
                const tagCategoriesDialog = this.matDialog.open<any, any, TagCategoriesDialogReturnType>(TagCategoriesDialogComponent,
                    this.getTagCategoriesDialogConfig());
                tagCategoriesDialog.afterClosed().subscribe(res => {
                    if (res && (res.add.length > 0 || res.update.length > 0 || res.delete.length > 0)) {
                        this.propagateTagCategoriesChanges(res);
                    }
                });
                return;
            }
            const selectedTags = value.selectedTags;
            if (!selectedTags || JSON.stringify(selectedTags) === JSON.stringify(position.tags)) {
                return;
            }
            const selectedTagIds = selectedTags.map(t => t.id);
            this.portfolioPositionService.updatePositionTags(position.id, selectedTagIds)
                .subscribe(() => {
                    this.store.updatePositionTags(position.id, selectedTags);
                });
        });
    }

    private getTagCategoriesDialogConfig() {
        return {
            restoreFocus: false,
            disableClose: false,
        };
    }

    private propagateTagCategoriesChanges(res: TagCategoriesDialogReturnType) {
        const whenCategoriesUpdated = res.update.map(c => this.tagCategoryService.updateTagCategory(c));
        const whenNewCategoriesCreated = res.add.map(c => this.tagCategoryService.createTagCategory(c));
        const whenCategoriesDeleted = res.delete.map(categoryId => this.tagCategoryService.deleteTagCategory(categoryId));
        forkJoin([...whenCategoriesUpdated, ...whenNewCategoriesCreated, ...whenCategoriesDeleted]).subscribe(() => {
            //TODO change it in future
            this.initPositionsStore();
        });
    }

    sortPositions(sort: Sort) {
        this.store.reorderExistingPositions(sort);
    }
}
