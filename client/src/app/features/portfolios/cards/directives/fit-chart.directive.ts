import { Directive, ElementRef, OnInit, Renderer2 } from '@angular/core';

@Directive({
  selector: '[appFitChart]'
})
export class FitChartDirective implements OnInit {

  constructor(private elRef: ElementRef, private renderer: Renderer2) {
  }

  ngOnInit() {
    this.renderer.setStyle(this.elRef.nativeElement, 'position', 'absolute');
    this.renderer.setStyle(this.elRef.nativeElement, 'top', '50%');
    this.renderer.setStyle(this.elRef.nativeElement, 'left', '50%');
    this.renderer.setStyle(this.elRef.nativeElement, 'transform', 'translate(-50%, -50%)');

    const parent = this.elRef.nativeElement.parentElement;
    this.renderer.setStyle(parent, 'position', 'absolute');
    this.renderer.setStyle(parent, 'top', '50px');
    this.renderer.setStyle(parent, 'left', '50px');
    this.renderer.setStyle(parent, 'right', '50px');
    this.renderer.setStyle(parent, 'bottom', '50px');
  }
}
