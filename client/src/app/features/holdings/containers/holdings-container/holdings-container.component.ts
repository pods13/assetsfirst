import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ColumnMode } from '@swimlane/ngx-datatable';
import { PortfolioHoldingService } from '../../services/portfolio-holding.service';

@Component({
  selector: 'app-holdings-container',
  template: `
    <ngx-datatable class="material" [rows]="holdings$ | async"
                   [columnMode]="ColumnMode.force"
                   [headerHeight]="headerHeight"
                   [rowHeight]="rowHeight"
                   [limit]="pageLimit"
                   [footerHeight]="footerHeight">
      <ngx-datatable-column [prop]="'identifier'">
        <ng-template let-value="value" ngx-datatable-cell-template>
          {{value.symbol}}
        </ng-template>
      </ngx-datatable-column>
      <ngx-datatable-column [prop]="'tags'" [name]="'Tags'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'quantity'" [name]="'Shares'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'price'" [name]="'Cost Per Share'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'percentage'" [name]="'% of Portfolio'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'total'" [name]="'Total Cost'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'marketValue'" [name]="'Market Value'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'recordDate'" [name]="'Record Date'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'yieldOnCost'" [name]="'Yield On Cost'"></ngx-datatable-column>
      <ngx-datatable-column [prop]="'realizedPnl'" [name]="'Realized P&L'"></ngx-datatable-column>
    </ngx-datatable>
  `,
  styleUrls: ['./holdings-container.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HoldingsContainerComponent implements OnInit {

  readonly headerHeight = 50;
  readonly footerHeight = 50;
  readonly rowHeight = 50;
  readonly pageLimit = 30;

  ColumnMode = ColumnMode;

  holdings$ = this.portfolioHoldingService.getPortfolioHoldings();

  constructor(private portfolioHoldingService: PortfolioHoldingService) {
  }

  ngOnInit(): void {
  }

}
