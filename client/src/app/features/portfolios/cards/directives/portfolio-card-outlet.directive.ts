import { Directive, Input, ViewContainerRef } from '@angular/core';
import { PortfolioCardDto } from '../types/portfolio-card.dto';

@Directive({
  selector: '[appPortfolioCardOutlet]'
})
export class PortfolioCardOutletDirective {
  @Input() card!: PortfolioCardDto;

  constructor(public viewContainerRef: ViewContainerRef) {
  }
}
