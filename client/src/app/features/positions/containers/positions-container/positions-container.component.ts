import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ColumnMode } from '@swimlane/ngx-datatable';
import { PortfolioPositionService } from '../../services/portfolio-position.service';

@Component({
  selector: 'app-positions-container',
  template: `
    <ngx-datatable class="material" [rows]="positions$ | async"
                   [columnMode]="ColumnMode.force"
                   [headerHeight]="headerHeight"
                   [rowHeight]="rowHeight"
                   [limit]="pageLimit"
                   [footerHeight]="footerHeight"
                   [sorts]="[{ prop: 'pctOfPortfolio', dir: 'desc' }]">
      <ngx-datatable-column [prop]="'identifier'">
        <ng-template let-value="value" ngx-datatable-cell-template>
          {{value.symbol}}
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'tags'" [name]="'Tags'"></ngx-datatable-column>
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
      <ngx-datatable-column [prop]="'exDividendDate'" [name]="'Ex-dividend Date'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'yieldOnCost'" [name]="'Yield On Cost'">
        <ng-template let-row="row" ngx-datatable-cell-template>
          {{row.yieldOnCost + '%'}}
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'realizedPnl'" [name]="'Realized P&L'"></ngx-datatable-column>
    </ngx-datatable>
  `,
  styleUrls: ['./positions-container.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PositionsContainerComponent implements OnInit {

  readonly headerHeight = 50;
  readonly footerHeight = 50;
  readonly rowHeight = 50;
  readonly pageLimit = 30;

  ColumnMode = ColumnMode;

  positions$ = this.portfolioPositionService.getPortfolioPositionsView();

  constructor(private portfolioPositionService: PortfolioPositionService) {
  }

  ngOnInit(): void {
  }
}
