import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ColumnMode } from '@swimlane/ngx-datatable';
import { TradeService } from '../../services/trade.service';
import { AddSecurityTradeDto } from '../../types/add-security-trade.dto';
import { AddMoneyTradeDto } from '../../types/money/add-money-trade.dto';
import { AddTradeDto } from '../../types/add-trade.dto';

@Component({
  selector: 'app-trades-container',
  template: `
    <app-datatable-actions-bar (trade)="onTrade($event)">
    </app-datatable-actions-bar>
    <ngx-datatable class="material" [rows]="trades$ | async"
                   [columns]="columns"
                   [columnMode]="ColumnMode.force"
                   [headerHeight]="headerHeight"
                   [rowHeight]="rowHeight">
    </ngx-datatable>
  `,
  styleUrls: ['./trades-container.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TradesContainerComponent implements OnInit {

  readonly headerHeight = 50;
  readonly rowHeight = 50;
  readonly pageLimit = 10;

  ColumnMode = ColumnMode;

  columns = [
    {prop: 'ticker'}, {prop: 'name'},
    {prop: 'operation'}, {prop: 'date'},
    {prop: 'quantity'}, {prop: 'price'}
  ];

  trades$ = this.tradeService.getUserTrades();

  constructor(private tradeService: TradeService,
              private cd: ChangeDetectorRef) {
  }

  ngOnInit(): void {
  }

  onTrade(dto: AddTradeDto) {
    console.log(typeof dto)
    this.tradeService.addTrade(dto)
      .subscribe(res => {
        this.trades$ = this.tradeService.getUserTrades();
        this.cd.detectChanges();
      });
  }
}
