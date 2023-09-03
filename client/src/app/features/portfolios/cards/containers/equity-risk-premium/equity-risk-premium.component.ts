import { ChangeDetectionStrategy, Component, OnInit, ViewChild } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { EquityRiskPremiumCard } from '../../types/in/equity-risk-premium-card';
import { EquityRiskPremiumCardData } from '../../types/out/equity-risk-premium-card-data';
import { map, Observable, shareReplay } from 'rxjs';
import { MatTable } from '@angular/material/table';

@Component({
  selector: 'app-equity-risk-premium',
  template: `
    <div class="card-header">
      <h2 class="title">{{ card.title }}</h2>
    </div>

    <div (mousedown)="$event.stopPropagation()" (touchstart)="$event.stopPropagation()">
      <mat-table #dataTable [dataSource]="dataSource$">

        <ng-container matColumnDef="name">
          <mat-header-cell *matHeaderCellDef>Name</mat-header-cell>
          <mat-cell *matCellDef="let element">{{element.name}}</mat-cell>
        </ng-container>

        <ng-container matColumnDef="riskPremium">
          <mat-header-cell *matHeaderCellDef>Premium</mat-header-cell>
          <mat-cell *matCellDef="let element"> {{element.riskPremium + '%'}}</mat-cell>
        </ng-container>

        <mat-header-row *matHeaderRowDef="displayedColumns; sticky: true"></mat-header-row>
        <mat-row *matRowDef="let row;columns: displayedColumns;"></mat-row>

      </mat-table>
    </div>
  `,
  styleUrls: ['./equity-risk-premium.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class EquityRiskPremiumComponent implements CardContainer<EquityRiskPremiumCard, EquityRiskPremiumCardData>, OnInit {

  card!: EquityRiskPremiumCard;
  data$!: Observable<EquityRiskPremiumCardData>;

  @ViewChild('dataTable') table!: MatTable<any>;
  displayedColumns: string[] = ['name', 'riskPremium'];
  dataSource$!: Observable<any[]>;

  constructor() {
  }

  ngOnInit(): void {
    this.dataSource$ = this.data$.pipe(map(data => data.equities), shareReplay({refCount: true, bufferSize: 1}));
  }

}
