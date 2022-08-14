import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ColumnMode, SelectionType } from '@swimlane/ngx-datatable';
import { TradeService } from '../../services/trade.service';
import { AddTradeDto } from '../../types/add-trade.dto';
import { TradeDto } from '../../types/trade.dto';
import { EditTradeDto } from '../../types/edit-trade.dto';
import { DeleteTradeDto } from '../../types/delete-trade.dto';
import { PageSort } from '../../../../core/types/page-sort';

@Component({
  selector: 'app-trades-container',
  template: `
    <app-datatable-actions-bar [selectedRows]="selectedRows"
                               (addTrade)="onAddTrade($event)"
                               (editTrade)="onEditTrade($event)"
                               (deleteTrade)="onDeleteTrade($event)">
    </app-datatable-actions-bar>
    <ngx-datatable class="material"
                   [rows]="rows"
                   [columnMode]="ColumnMode.force"
                   [headerHeight]="headerHeight"
                   [rowHeight]="rowHeight"
                   [limit]="pageSize"
                   [footerHeight]="50"
                   [sorts]="pageSorts"
                   [selectionType]="SelectionType.checkbox"
                   [selectAllRowsOnPage]="true"
                   [selected]="selectedRows"
                   (select)="onSelect($event)"
                   [externalPaging]="true"
                   [externalSorting]="true"
                   [count]="totalElements"
                   [offset]="pageNumber"
                   (page)="setPage($event)"
                   (sort)="onSort($event)">
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
  readonly pageSize = 10;

  totalElements!: number;
  firstPageNumber = 0;
  pageNumber = this.firstPageNumber;


  pageSorts = [{prop: 'date', dir: 'desc'}];

  ColumnMode = ColumnMode;
  SelectionType = SelectionType;

  columns = [
    {prop: 'ticker'}, {prop: 'name'},
    {prop: 'operation'}, {prop: 'date'},
    {prop: 'quantity'}, {prop: 'price'},
    {prop: 'brokerName'}
  ];

  rows: TradeDto[] = [];
  selectedRows: TradeDto[] = [];

  constructor(private tradeService: TradeService,
              private cd: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.setPage({pageSize: this.pageSize, offset: this.pageNumber});
  }

  setPage(pageInfo: { pageSize: number; offset: number; sorts?: PageSort[] }) {
    const pageNumber = pageInfo.offset;
    this.tradeService.getUserTrades({
      size: pageInfo.pageSize, page: pageNumber + 1,
      sorts: pageInfo.sorts ?? this.pageSorts
    }).subscribe((page => {
      this.totalElements = page.totalElements;
      this.rows = page.content;
      this.pageNumber = pageNumber;
      this.cd.detectChanges();
    }));
  }

  onAddTrade(dto: AddTradeDto) {
    this.selectedRows = [];
    this.tradeService.addTrade(dto)
      .subscribe(res => {
        this.setPage({pageSize: this.pageSize, offset: this.firstPageNumber});
      });
  }

  onSelect($event: any) {
    this.selectedRows = [...$event.selected];
  }

  onEditTrade(dto: EditTradeDto) {
    this.tradeService.editTrade(dto)
      .subscribe(res => {
        this.selectedRows = [];
        this.setPage({pageSize: this.pageSize, offset: this.pageNumber});
      });
  }

  onDeleteTrade(dto: DeleteTradeDto) {
    this.tradeService.deleteTrade(dto)
      .subscribe(res => {
        this.selectedRows = [];
        this.setPage({pageSize: this.pageSize, offset: this.firstPageNumber});
      });
  }

  onSort({sorts}: { sorts: PageSort[] }) {
    this.setPage({pageSize: this.pageSize, offset: this.firstPageNumber, sorts})
  }
}
