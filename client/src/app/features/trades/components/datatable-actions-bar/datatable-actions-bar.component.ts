import { ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { TradeDialogComponent } from '../../containers/trade-dialog/trade-dialog.component';
import { filter } from 'rxjs';
import { AddTradeDto } from '../../types/add-trade.dto';

@Component({
  selector: 'app-datatable-actions-bar',
  template: `
    <button mat-button (click)="openTradeDialog()">
      <mat-icon>add</mat-icon>
      {{'Add Trade'}}
    </button>
  `,
  styleUrls: ['./datatable-actions-bar.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DatatableActionsBarComponent implements OnInit {

  @Output()
  trade = new EventEmitter<AddTradeDto>();

  constructor(private dialog: MatDialog) {
  }

  ngOnInit(): void {
  }

  openTradeDialog() {
    const dialogRef = this.dialog.open<TradeDialogComponent, any, AddTradeDto>(TradeDialogComponent, {});

    dialogRef.afterClosed()
      .pipe(filter(res => !!res))
      .subscribe(result => {
        this.trade.emit(result);
      });
  }
}
