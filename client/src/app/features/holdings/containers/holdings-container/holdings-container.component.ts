import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';

@Component({
  selector: 'app-holdings-container',
  template: `
    <p>
      holdings-container works!
    </p>
  `,
  styleUrls: ['./holdings-container.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class HoldingsContainerComponent implements OnInit {

  constructor() { }

  ngOnInit(): void {
  }

}
