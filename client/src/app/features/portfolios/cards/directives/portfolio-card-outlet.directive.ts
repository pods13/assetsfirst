import { Directive, Input, ViewContainerRef } from '@angular/core';
import { DashboardCard } from '../types/dashboard-card';

@Directive({
  selector: '[appPortfolioCardOutlet]'
})
export class PortfolioCardOutletDirective {
  @Input() card!: DashboardCard;

  constructor(public viewContainerRef: ViewContainerRef) {
  }
}
