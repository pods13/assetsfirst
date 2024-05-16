import {ChangeDetectionStrategy, Component, EventEmitter, OnInit, Output} from '@angular/core';
import {SelectCardDialogComponent} from '../../cards/components/select-card-dialog/select-card-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {CardContainerType} from '../../cards/types/card-container-type';
import {filter} from 'rxjs';

@Component({
    selector: 'app-dashboard-actions-bar',
    template: `
    <button mat-button (click)="onAddNewCard()">+ Add New Card</button>
  `,
    styleUrls: ['./dashboard-actions-bar.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class DashboardActionsBarComponent implements OnInit {

    @Output()
    addNewCard = new EventEmitter<CardContainerType>();

    constructor(private dialog: MatDialog) {
    }

    ngOnInit(): void {
    }

    onAddNewCard() {
        const dialogRef = this.dialog.open(SelectCardDialogComponent, {
            width: '50%',
            data: {
                cards: this.getAvailableToSelectCards()
            }
        });

        dialogRef.afterClosed().pipe(filter(res => !!res))
            .subscribe(result => {
                this.addNewCard.emit(result);
            });
    }

    getAvailableToSelectCards() {
        return Object.keys(CardContainerType).filter(c => c !== 'DIVIDEND_GOALS');
    }
}
