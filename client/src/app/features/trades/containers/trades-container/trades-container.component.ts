import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ColumnMode, SelectionType } from '@swimlane/ngx-datatable';
import { TradeService } from '../../services/trade.service';
import { AddTradeDto } from '../../types/add-trade.dto';
import { TradeDto } from '../../types/trade.dto';
import { EditTradeDto } from '../../types/edit-trade.dto';

@Component({
  selector: 'app-trades-container',
  template: `
    <app-datatable-actions-bar [selectedRows]="selectedRows"
                               (addTrade)="onAddTrade($event)"
                               (editTrade)="onEditTrade($event)">
    </app-datatable-actions-bar>
    <ngx-datatable class="material" [rows]="trades$ | async"
                   [columnMode]="ColumnMode.force"
                   [headerHeight]="headerHeight"
                   [rowHeight]="rowHeight"
                   [limit]="pageLimit"
                   [footerHeight]="50"
                   [sorts]="[{ prop: 'date', dir: 'desc' }]"
                   [selectionType]="SelectionType.checkbox"
                   [selectAllRowsOnPage]="true"
                   (select)="onSelect($event)">
      <ngx-datatable-column
        [width]="30"
        [sortable]="false"
        [canAutoResize]="false"
        [draggable]="false"
        [resizeable]="false"
        [headerCheckboxable]="true"
        [checkboxable]="true">
      </ngx-datatable-column>
      <ngx-datatable-column *ngFor="let col of columns" [prop]="col.prop"></ngx-datatable-column>
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
  SelectionType = SelectionType;

  columns = [
    {prop: 'ticker'}, {prop: 'name'},
    {prop: 'operation'}, {prop: 'date'},
    {prop: 'quantity'}, {prop: 'price'}
  ];

  trades$ = this.tradeService.getUserTrades();

  selectedRows: TradeDto[] = [];

  constructor(private tradeService: TradeService,
              private cd: ChangeDetectorRef) {
  }

  ngOnInit(): void {
  }

  onAddTrade(dto: AddTradeDto) {
    this.selectedRows = [];
    this.tradeService.addTrade(dto)
      .subscribe(res => {
        this.trades$ = this.tradeService.getUserTrades();
        this.cd.detectChanges();
      });
  }

  onSelect($event: any) {
    this.selectedRows = [...$event.selected];
  }

  onEditTrade(dto: EditTradeDto) {
    this.tradeService.editTrade(dto)
      .subscribe(res => {
        this.trades$ = this.tradeService.getUserTrades();
        this.cd.detectChanges();
      });
  }
}
