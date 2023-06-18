import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ColumnMode } from '@swimlane/ngx-datatable';
import { PortfolioPositionService } from '../../services/portfolio-position.service';
import { MatDialog } from '@angular/material/dialog';
import {
  PositionTagsDialogComponent,
  PositionTagsDialogData,
  PositionTagsDialogReturnType
} from '../../components/tags-dialog/position-tags-dialog.component';
import { PortfolioPositionView } from '../../types/portfolio-position.view';
import { stringifyTicker } from '../../../../core/types/ticker';
import {
  TagCategoriesDialogComponent,
  TagCategoriesDialogReturnType
} from '../../components/tag-categories-dialog/tag-categories-dialog.component';
import { TagCategoryService } from '../../services/tag-category.service';
import { forkJoin } from 'rxjs';
import { MatSlideToggleChange } from '@angular/material/slide-toggle';

@Component({
  selector: 'app-positions-container',
  template: `
    <div class="positions-header">
      <mat-slide-toggle [(ngModel)]="hideSoldPositions" (change)="onHideSoldToggled($event)">Hide Sold</mat-slide-toggle>
    </div>
    <ngx-datatable class="material" [rows]="positions$ | async"
                   [columnMode]="ColumnMode.standard"
                   [headerHeight]="headerHeight"
                   [rowHeight]="rowHeight"
                   [limit]="pageLimit"
                   [footerHeight]="footerHeight"
                   [sorts]="[{ prop: 'pctOfPortfolio', dir: 'desc' }]">
      <ngx-datatable-column [prop]="'identifier'">
        <ng-template let-row="row" ngx-datatable-cell-template>
          <div class="identifier">
            <a class="identifier-symbol" [routerLink]="'/symbols/' + row.identifier.exchange + '-' + row.identifier.symbol">{{row.identifier.symbol}}</a>
            <div class="identifier-company">{{row.companyName}}</div>
          </div>
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'tags'" [name]="'Tags'">
        <ng-template let-row="row" ngx-datatable-cell-template>
          <ng-container *ngIf="row.tags.length === 0; else tags">
            <button mat-button (click)="openPositionTagsDialog($event, row)">
              <mat-icon>library_add</mat-icon>
            </button>
          </ng-container>
          <ng-template #tags>
            <button mat-button (click)="openPositionTagsDialog($event, row)">
              <mat-icon [matBadge]="row.tags.length" matBadgeColor="warn">tag</mat-icon>
              <span class="cdk-visually-hidden">{{row.tags.length + ' tags linked to this position'}}</span>
            </button>
          </ng-template>
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'quantity'" [name]="'Shares'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'price'" [name]="'Cost Per Share'">
        <ng-template let-row="row" ngx-datatable-cell-template>
          {{row.price | currency: row.currencySymbol}}
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'pctOfPortfolio'" [name]="'% of Portfolio'">
        <ng-template let-value="value" ngx-datatable-cell-template>
          {{value + '%'}}
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'total'" [name]="'Total Cost'">
        <ng-template let-row="row" ngx-datatable-cell-template>
          {{row.total | currency: row.currencySymbol}}
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'marketValue'" [name]="'Market Value'">
        <ng-template let-row="row" ngx-datatable-cell-template>
          {{row.marketValue | currency: row.currencySymbol}}
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'yieldOnCost'" [name]="'Yield On Cost'">
        <ng-template let-row="row" ngx-datatable-cell-template>
          {{row.yieldOnCost + '%'}}
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'accumulatedDividends'" [name]="'Accumulated Dividends'">
        <ng-template let-row="row" ngx-datatable-cell-template>
          {{row.accumulatedDividends | currency: row.currencySymbol}}
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'upcomingDividendDate'" [name]="'Upcoming Dividend Date'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'realizedPnl'" [name]="'Realized P&L'"></ngx-datatable-column>
    </ngx-datatable>
  `,
  styleUrls: ['./positions-container.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PositionsContainerComponent implements OnInit {

  readonly headerHeight = 50;
  readonly footerHeight = 50;
  readonly rowHeight = 60;
  readonly pageLimit = 30;

  ColumnMode = ColumnMode;

  hideSoldPositions = true;
  positions$ = this.getPortfolioPositions();

  constructor(private portfolioPositionService: PortfolioPositionService,
              private tagCategoryService: TagCategoryService,
              private matDialog: MatDialog) {
  }

  ngOnInit(): void {
  }

  openPositionTagsDialog(event: MouseEvent, position: PortfolioPositionView) {
    const dialogRef = this.matDialog.open<any, PositionTagsDialogData, PositionTagsDialogReturnType>(PositionTagsDialogComponent, {
      height: '100vh',
      width: '100vw',
      maxWidth: '512px',
      position: {
        top: '0',
        right: '0'
      },
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
          console.log(res)
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
          this.positions$ = this.getPortfolioPositions();
        });
    });
  }

  private getTagCategoriesDialogConfig() {
    return {
      height: '100vh',
      width: '100vw',
      maxWidth: '512px',
      position: {
        top: '0',
        right: '0'
      },
      restoreFocus: false,
      disableClose: false,
    };
  }

  private propagateTagCategoriesChanges(res: TagCategoriesDialogReturnType) {
    const whenCategoriesUpdated = res.update.map(c => this.tagCategoryService.updateTagCategory(c));
    const whenNewCategoriesCreated = res.add.map(c => this.tagCategoryService.createTagCategory(c));
    const whenCategoriesDeleted = res.delete.map(categoryId => this.tagCategoryService.deleteTagCategory(categoryId));
    forkJoin([...whenCategoriesUpdated, ...whenNewCategoriesCreated, ...whenCategoriesDeleted]).subscribe(() => {
      this.positions$ = this.getPortfolioPositions();
    });
  }

  getPortfolioPositions() {
    return this.portfolioPositionService.getPortfolioPositionsView(this.hideSoldPositions);
  }

  onHideSoldToggled({checked}: MatSlideToggleChange) {
    this.positions$ = this.getPortfolioPositions();
  }
}
