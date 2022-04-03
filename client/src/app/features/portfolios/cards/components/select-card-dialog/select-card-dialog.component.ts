import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { CardContainerType } from '../../types/card-container-type';

@Component({
  selector: 'app-select-widget-dialog',
  template: `
    <h1 mat-dialog-title>Select Card to add</h1>
    <div mat-dialog-content>
      <mat-form-field class="widget-selector">
        <mat-select [(ngModel)]="selectedCard">
          <mat-option *ngFor="let card of data.cards" [value]="card">
            {{card}}
          </mat-option>
        </mat-select>
      </mat-form-field>
    </div>
    <div mat-dialog-actions>
      <button mat-button [mat-dialog-close]="selectedCard">Ok</button>
      <button mat-button [mat-dialog-close]="null">Cancel</button>
    </div>
  `,
  styleUrls: ['./select-card-dialog.component.scss']
})
export class SelectCardDialogComponent implements OnInit {

  selectedCard: CardContainerType;

  constructor(public dialogRef: MatDialogRef<SelectCardDialogComponent>,
              @Inject(MAT_DIALOG_DATA) public data: SelectCardDialogData) {
    this.selectedCard = data.cards[0];
  }

  ngOnInit() {
  }
}

export interface SelectCardDialogData {
  cards: CardContainerType[];
}
