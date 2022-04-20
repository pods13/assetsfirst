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
import { PortfolioCard } from '../../types/portfolio-card';
import { PortfolioCardOutletDirective } from '../../directives/portfolio-card-outlet.directive';
import { CardContentLoaderService } from '../../services/card-content-loader.service';
import { RxStompService } from '../../../../../core/services/rx-stomp.service';
import { map, Observable, shareReplay } from 'rxjs';
import { UntilDestroy, untilDestroyed } from '@ngneat/until-destroy';

@UntilDestroy()
@Component({
  selector: 'app-card-wrapper',
  template: `
    <div class="card-header">
      <div class="title">{{ card?.title }}</div>
      <button class="btn" mat-icon-button aria-label="Configure Card" [matMenuTriggerFor]="menu">
        <mat-icon>more_vert</mat-icon>
      </button>
      <mat-menu #menu="matMenu">
        <button mat-menu-item>Edit</button>
        <button mat-menu-item>Delete</button>
      </mat-menu>
    </div>
    <div class="card-body">
      <ng-template appPortfolioCardOutlet [card]="card">
      </ng-template>
    </div>
  `,
  styleUrls: ['./card-wrapper.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class CardWrapperComponent implements OnInit, AfterViewInit, OnChanges {

  @ViewChild(PortfolioCardOutletDirective, {static: true}) cardOutlet!: PortfolioCardOutletDirective;

  @Input()
  card!: PortfolioCard;

  cardData$!: Observable<any>;

  constructor(private cardContentLoaderService: CardContentLoaderService,
              private rxStompService: RxStompService,
              private cd: ChangeDetectorRef) {
  }

  ngOnInit(): void {
    this.cardData$ = this.getCardData(this.card);
  }

  ngAfterViewInit(): void {
    this.cardContentLoaderService.loadContent(this.cardOutlet, this.cardData$);
    this.cd.detectChanges();
    this.rxStompService.publish({
      destination: '/app/cards',
      body: JSON.stringify(this.card)
    });
  }

  ngOnChanges(changes: SimpleChanges): void {
    const cardChanges = changes['card'];
    if (!cardChanges || cardChanges.firstChange) {
      return;
    }
    if (cardChanges.currentValue?.rows !== cardChanges.previousValue?.rows
      || cardChanges.currentValue?.cols !== cardChanges.previousValue?.cols) {
      this.cardContentLoaderService.loadContent(this.cardOutlet, this.cardData$);
      this.cd.detectChanges();
    }
    //TODO Publish new card whenever specific to it props changed - use Object.keys(defaultCardProps) on cardFactory to check if props remained the same -> fire ws event
  }

  private getCardData(card: PortfolioCard) {
    return this.rxStompService.watch(`/user/topic/cards/${card.id}`)
      .pipe(
        untilDestroyed(this),
        map(message => JSON.parse(message.body)),
        shareReplay(1)
      );
  };

}
