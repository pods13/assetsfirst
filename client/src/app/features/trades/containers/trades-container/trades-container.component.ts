import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit, ViewChild} from '@angular/core';
import {TradeService} from '../../services/trade.service';
import {AddTradeDto} from '../../types/add-trade.dto';
import {TradeViewDto} from '../../types/trade-view.dto';
import {EditTradeDto} from '../../types/edit-trade.dto';
import {DeleteTradeDto} from '../../types/delete-trade.dto';
import {PageSort} from '../../../../core/types/page-sort';
import {MatPaginator, PageEvent} from '@angular/material/paginator';
import {MatSort, Sort} from '@angular/material/sort';
import {SelectionModel} from '@angular/cdk/collections';

@Component({
    selector: 'app-trades-container',
    template: `
    <app-datatable-actions-bar [selectedRows]="selectedRows"
                               (addTrade)="onAddTrade($event)"
                               (editTrade)="onEditTrade($event)"
                               (deleteTrade)="onDeleteTrade($event)">
    </app-datatable-actions-bar>
    <div class="mat-elevation-z8">
      <table mat-table [dataSource]="rows" matSort (matSortChange)="announceSortChange($event)"
             matSortActive="date" matSortDirection="desc">
        <ng-container matColumnDef="select">
          <th mat-header-cell *matHeaderCellDef>
          </th>
          <td mat-cell *matCellDef="let row">
            <mat-checkbox (click)="$event.stopPropagation()"
                          (change)="$event ? selection.toggle(row) : null"
                          [checked]="selection.isSelected(row)">
            </mat-checkbox>
          </td>
        </ng-container>
        <ng-container matColumnDef="symbol">
          <th mat-header-cell *matHeaderCellDef> Symbol</th>
          <td mat-cell *matCellDef="let element"> {{element.symbol}} </td>
        </ng-container>

        <ng-container matColumnDef="name">
          <th mat-header-cell *matHeaderCellDef mat-sort-header> Name</th>
          <td mat-cell *matCellDef="let element"> {{element.name}} </td>
        </ng-container>

        <ng-container matColumnDef="operation">
          <th mat-header-cell *matHeaderCellDef>Operation</th>
          <td mat-cell *matCellDef="let element"> {{element.operation}} </td>
        </ng-container>

        <ng-container matColumnDef="date">
          <th mat-header-cell *matHeaderCellDef mat-sort-header>Date</th>
          <td mat-cell *matCellDef="let element"> {{element.date}} </td>
        </ng-container>

        <ng-container matColumnDef="quantity">
          <th mat-header-cell *matHeaderCellDef>Quantity</th>
          <td mat-cell *matCellDef="let element"> {{element.quantity}} </td>
        </ng-container>

        <ng-container matColumnDef="price">
          <th mat-header-cell *matHeaderCellDef>Price</th>
          <td mat-cell *matCellDef="let element"> {{element.price}} </td>
        </ng-container>

          <ng-container matColumnDef="intermediary">
              <th mat-header-cell *matHeaderCellDef>Intermediary</th>
              <td mat-cell *matCellDef="let element"> {{element.intermediaryName}} </td>
          </ng-container>

        <tr mat-header-row *matHeaderRowDef="displayedColumns"></tr>
        <tr mat-row *matRowDef="let row; columns: displayedColumns;" (click)="selection.toggle(row)"></tr>
      </table>

      <mat-paginator #paginator showFirstLastButtons
                     aria-label="Select page of trades"
                     [length]="totalElements"
                     [pageIndex]="pageNumber"
                     [pageSize]="pageSize"
                     (page)="onPageChanged($event)">
      </mat-paginator>
    </div>
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

    @ViewChild(MatPaginator) paginator!: MatPaginator;
    @ViewChild(MatSort) sort!: MatSort;

    displayedColumns = ['select', 'symbol', 'name', 'operation', 'date', 'quantity', 'price', 'intermediary'];
    selection = new SelectionModel<TradeViewDto>(false, []);

    rows: TradeViewDto[] = [];
    selectedRows: TradeViewDto[] = [];

    constructor(private tradeService: TradeService,
                private cd: ChangeDetectorRef) {
    }

    ngOnInit(): void {
        this.setPage({pageSize: this.pageSize, offset: this.pageNumber});
        this.selection.changed.subscribe(selectionChange => {
            if (selectionChange) {
                this.selectedRows = [...selectionChange.added];
            }
        });
    }

    setPage(pageInfo: { pageSize: number; offset: number; sorts?: PageSort[] }) {
        const pageNumber = pageInfo.offset;
        this.tradeService.getUserTrades({
            size: pageInfo.pageSize, page: pageNumber + 1,
            sorts: [...(pageInfo.sorts ?? this.pageSorts), {prop: 'id', dir: 'desc'}]
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

    onPageChanged(event: PageEvent) {
        this.setPage({pageSize: event.pageSize, offset: event.pageIndex});
    }

    announceSortChange(event: Sort) {
        this.setPage({
            pageSize: this.pageSize,
            offset: this.firstPageNumber,
            sorts: [{prop: event.active, dir: event.direction}]
        });
    }
}
