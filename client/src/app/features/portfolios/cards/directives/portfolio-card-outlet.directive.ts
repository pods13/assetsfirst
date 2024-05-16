import {Directive, ViewContainerRef} from '@angular/core';

@Directive({
    selector: '[appPortfolioCardOutlet]'
})
export class PortfolioCardOutletDirective {
    constructor(public viewContainerRef: ViewContainerRef) {
    }
}
