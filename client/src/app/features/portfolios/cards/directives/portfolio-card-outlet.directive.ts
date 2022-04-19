import { Directive, Input, ViewContainerRef } from '@angular/core';
import { PortfolioCard } from '../types/portfolio-card';

@Directive({
  selector: '[appPortfolioCardOutlet]'
})
export class PortfolioCardOutletDirective {
  @Input() card!: PortfolioCard;

  constructor(public viewContainerRef: ViewContainerRef) {
  }
}
