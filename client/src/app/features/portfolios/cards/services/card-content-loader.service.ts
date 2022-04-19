import { PortfolioCardOutletDirective } from '../directives/portfolio-card-outlet.directive';
import { Injectable } from '@angular/core';
import { PortfolioCard } from '../types/portfolio-card';
import { cardContainerTemplateMapper } from '../types/card-container-template-mapper';
import { CardContainer } from '../types/card-container';
import { Observable, tap } from 'rxjs';
import { CardData } from '../types/card-data';

@Injectable()
export class CardContentLoaderService {

  constructor() {
  }

  loadContent(template: PortfolioCardOutletDirective, cardData$: Observable<any>): void {
    if (!template.card) {
      return;
    }

    const viewContainerRef = template.viewContainerRef;
    viewContainerRef.clear();
    const containerType = template.card.containerType;
    const containerClass = cardContainerTemplateMapper[containerType];
    if (!containerClass) {
      console.error(`You forgot to add ${containerType} to card-container-template-mapper.ts`);
      return;
    }
    const componentRef = viewContainerRef.createComponent(containerClass)
    const instance = componentRef.instance as CardContainer<PortfolioCard, CardData>;
    instance.card = template.card;
    instance.data$ = cardData$.pipe(tap(data => instance.tapIntoData?.(data)));
  }
}
