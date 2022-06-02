import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ColumnMode } from '@swimlane/ngx-datatable';
import { PortfolioHoldingService } from '../../services/portfolio-holding.service';
import { FundamentalsService } from '../../services/fundamentals.service';
import { forkJoin, map } from 'rxjs';
import { PortfolioHoldingDto } from '../../types/portfolio-holding,dto';

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
      <ngx-datatable-column [prop]="'pctOfPortfolio'" [name]="'% of Portfolio'">
        <ng-template let-value="value" ngx-datatable-cell-template>
          {{value + '%'}}
        </ng-template>
      </ngx-datatable-column>
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

  holdings$ = forkJoin([
    this.portfolioHoldingService.getPortfolioHoldings(),
    this.fundamentalsService.getHoldingsFundamentals()
  ]).pipe(map(([holdings, fundamentals]) => {
    return this.composeTableRows(holdings, fundamentals);
  }));

  constructor(private portfolioHoldingService: PortfolioHoldingService,
              private fundamentalsService: FundamentalsService) {
  }

  ngOnInit(): void {
  }

  composeTableRows(holdings: PortfolioHoldingDto[], fundamentals: any[]) {
    const fundByIds = fundamentals.reduce((acc, fund) => {
      const {identifier} = fund;
      const key = identifier.symbol + '.' + identifier.exchange;
      return {...acc, [key]: fund};
    }, {});
    const portfolioMarketValue = fundamentals.reduce((res, fund) => res += fund.convertedMarketValue, 0);
    let pctTotal = 0;
    return holdings.map((holding, index) => {
      const {identifier} = holding;
      const key = identifier.symbol + '.' + identifier.exchange;
      const marketValue = fundByIds[key].marketValue;
      const converted = fundByIds[key].convertedMarketValue;
      const pctOfPortfolio = index === holdings.length ? 100 - pctTotal : + ((100 * converted) / portfolioMarketValue).toFixed(1);
      pctTotal += pctOfPortfolio;
      return {...holding, marketValue, pctOfPortfolio};
    });
  }
}
