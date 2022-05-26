import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';
import { SectoralDistributionCardData } from '../../types/out/sectoral-distribution-card-data';
import { TreeMapDataItem } from '@swimlane/ngx-charts';
import { DataItem } from '@swimlane/ngx-charts/lib/models/chart-data.model';
import { DashboardCard } from '../../types/dashboard-card';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-sectoral-distribution-card',
  template: `
    <ng-container *ngIf="data$ | async as data">
      <div class="breadcrumbs" *ngIf="treemapPath.length > 1">
        <ng-container *ngFor="let item of treemapPath; let last = last">
          <button mat-button [class.active]="last" [disabled]="last" (click)="treemapSelect(item)">
            {{ item.name }}
          </button>
          <span *ngIf="!last"> / </span>
        </ng-container>
      </div>
      <div class="tree-wrapper">
        <ngx-charts-tree-map appFitChart
                             [scheme]="'vivid'"
                             [results]="treemap"
                             [animations]="true"
                             (select)="treemapSelect($event)">
        </ngx-charts-tree-map>
      </div>
    </ng-container>
  `,
  styleUrls: ['./sectoral-distribution-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SectoralDistributionCardComponent implements CardContainer<DashboardCard, SectoralDistributionCardData>, OnInit {

  card!: DashboardCard;
  data$!: Observable<SectoralDistributionCardData>;

  treemap!: any[];
  treemapPath: any[] = [];

  constructor() {
  }

  ngOnInit(): void {
    this.data$.pipe(untilDestroyed(this))
      .subscribe(data => {
        this.treemap = [...data.items];
        this.treemapPath = [{ name: 'All', children: [...this.treemap], value: -1 }];
      });
  }

  treemapSelect(item: any) {
    console.log(item)
    if (item.children) {
      const idx = this.treemapPath.indexOf(item);
      this.treemapPath.splice(idx + 1);
      this.treemap = this.treemapPath[idx].children;
      return;
    }
    const node = this.treemap.find(d => d.name === item.name);
    if (node.children) {
      this.treemapPath.push(node);
      this.treemap = node.children;
    }
  }

}
