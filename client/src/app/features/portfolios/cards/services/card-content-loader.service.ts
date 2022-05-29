import { PortfolioCardOutletDirective } from '../directives/portfolio-card-outlet.directive';
import { Injectable } from '@angular/core';
import { DashboardCard } from '../types/dashboard-card';
import { cardContainerTemplateMapper } from '../types/card-container-template-mapper';
import { CardContainer } from '../types/card-container';
import { Observable } from 'rxjs';
import { CardData } from '../types/card-data';

@Injectable()
export class CardContentLoaderService {

  constructor() {
  }

  loadContent(template: PortfolioCardOutletDirective, card: DashboardCard, cardData$: Observable<any>, onCardChanges: any): void {
    if (!card) {
      return;
    }

    const viewContainerRef = template.viewContainerRef;
    viewContainerRef.clear();
    const containerType = card.containerType;
    const containerClass = cardContainerTemplateMapper[containerType] as any;
    if (!containerClass) {
      console.error(`You forgot to add ${containerType} to card-container-template-mapper.ts`);
      return;
    }
    const componentRef = viewContainerRef.createComponent(containerClass)
    const instance = componentRef.instance as CardContainer<DashboardCard, CardData>;
    instance.card = card;
    instance.data$ = cardData$;
    instance.cardChanges$?.subscribe((card) => onCardChanges(card));
  }
}
