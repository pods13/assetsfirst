import {
  AfterViewInit,
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  Input,
  OnChanges,
  OnInit,
  SimpleChanges,
  ViewChild
} from '@angular/core';
import { DashboardCard } from '../../types/dashboard-card';
import { PortfolioCardOutletDirective } from '../../directives/portfolio-card-outlet.directive';
import { CardContentLoaderService } from '../../services/card-content-loader.service';
import { RxStompService } from '../../../../../core/services/rx-stomp.service';
import { map, Observable, shareReplay } from 'rxjs';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';
import { defaultCardProps } from '../../helpers/card-factory';
import { DashboardCardStore } from '../../services/dashboard-card.store';

@UntilDestroy()
@Component({
  selector: 'app-card-wrapper',
  template: `
    <button class="btn" mat-icon-button aria-label="Configure Card" [matMenuTriggerFor]="menu">
      <mat-icon>more_vert</mat-icon>
    </button>
    <mat-menu #menu="matMenu">
      <button mat-menu-item>Edit</button>
      <button mat-menu-item (click)="deleteCard()">Delete</button>
    </mat-menu>
    <ng-template appPortfolioCardOutlet>
    </ng-template>
  `,
  styleUrls: ['./card-wrapper.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CardWrapperComponent implements OnInit, AfterViewInit, OnChanges {

  @ViewChild(PortfolioCardOutletDirective, {static: true}) cardOutlet!: PortfolioCardOutletDirective;

  @Input()
  card!: DashboardCard;

  cardData$!: Observable<any>;

  constructor(private cardContentLoaderService: CardContentLoaderService,
              private rxStompService: RxStompService,
              private cardStore: DashboardCardStore,
              private cd: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.cardData$ = this.getCardData(this.card);
  }

  ngAfterViewInit(): void {
    this.cardContentLoaderService.loadContent(this.cardOutlet, this.card, this.cardData$, this.onCardChanges);
    this.cd.detectChanges();
    this.publishCard(this.card);
  }

  onCardChanges = (card: DashboardCard) => {
    this.cardStore.updateCard(card);
  }

  private publishCard(card: DashboardCard): void {
    this.rxStompService.publish({
      destination: '/app/cards',
      body: JSON.stringify(card)
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    const cardChanges = changes['card'];
    if (!cardChanges || cardChanges.firstChange) {
      return;
    }
    if (cardChanges.currentValue?.rows !== cardChanges.previousValue?.rows
      || cardChanges.currentValue?.cols !== cardChanges.previousValue?.cols) {
      this.cardContentLoaderService.loadContent(this.cardOutlet, this.card, this.cardData$, this.onCardChanges);
      this.cd.detectChanges();
      return;
    }
    if (Object.keys(defaultCardProps).every(key => cardChanges.currentValue[key] === cardChanges.previousValue[key])) {
      this.cardContentLoaderService.loadContent(this.cardOutlet, this.card, this.cardData$, this.onCardChanges);
      this.cd.detectChanges();
      this.publishCard(this.card);
    }
  }

  private getCardData(card: DashboardCard) {
    return this.rxStompService.watch(`/user/topic/cards/${card.id}`)
      .pipe(
        untilDestroyed(this),
        map(message => JSON.parse(message.body)),
        shareReplay(1)
      );
  };

  deleteCard(): void {
    this.cardStore.deleteCard(this.card);
  }
}
