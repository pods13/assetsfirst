import {ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output} from '@angular/core';
import {MatDialog} from '@angular/material/dialog';
import {TradeDialogComponent} from '../../containers/trade-dialog/trade-dialog.component';
import {filter} from 'rxjs';
import {AddTradeDto} from '../../types/add-trade.dto';
import {TradeViewDto} from '../../types/trade-view.dto';
import {EditTradeDto} from '../../types/edit-trade.dto';
import {DeleteTradeDto} from '../../types/delete-trade.dto';

@Component({
    selector: 'app-datatable-actions-bar',
    template: `
    <button mat-button (click)="openAddTradeDialog()">
      <mat-icon>add</mat-icon>
      {{'Add Trade'}}
    </button>
    <ng-container *ngIf="selectedRows.length === 1">
      <button mat-button (click)="openEditTradeDialog(selectedRows[0])">
        <mat-icon>edit</mat-icon>
        {{'Edit Trade'}}
      </button>
      <button mat-button (click)="handleDeleteTrade(selectedRows[0])">
        <mat-icon>delete</mat-icon>
        {{'Delete Trade'}}
      </button>
    </ng-container>
  `,
    styleUrls: ['./datatable-actions-bar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatatableActionsBarComponent implements OnInit {

    @Input()
    selectedRows!: TradeViewDto[];

    @Output()
    addTrade = new EventEmitter<AddTradeDto>();
    @Output()
    editTrade = new EventEmitter<EditTradeDto>();
    @Output()
    deleteTrade = new EventEmitter<DeleteTradeDto>();

    constructor(private dialog: MatDialog) {
    }

    ngOnInit(): void {
    }

    openAddTradeDialog() {
        const dialogRef = this.dialog.open<TradeDialogComponent, any, AddTradeDto>(TradeDialogComponent, {
            data: {
                title: 'Add New Trade'
            }
        });

        dialogRef.afterClosed()
            .pipe(filter(res => !!res))
            .subscribe(result => {
                this.addTrade.emit(result);
            });
    }

    openEditTradeDialog(trade: TradeViewDto) {
        const dialogRef = this.dialog.open<TradeDialogComponent, any, EditTradeDto>(TradeDialogComponent, {
            data: {
                title: 'Edit Trade',
                trade
            }
        });

        dialogRef.afterClosed()
            .pipe(filter(res => !!res))
            .subscribe(result => {
                this.editTrade.emit(result);
            });
    }

    handleDeleteTrade(trade: TradeViewDto): void {
        const tradeToDelete = {
            tradeId: trade.id,
            instrumentId: trade.instrumentId,
            instrumentType: trade.instrumentType
        } as DeleteTradeDto;
        this.deleteTrade.emit(tradeToDelete);
    }
}
