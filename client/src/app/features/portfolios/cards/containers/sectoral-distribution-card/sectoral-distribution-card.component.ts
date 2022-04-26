import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-sectoral-distribution-card',
  template: `
    <p>
      sectoral-distribution-card works!
    </p>
  `,
  styleUrls: ['./sectoral-distribution-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SectoralDistributionCardComponent implements CardContainer<any, any>, OnInit {

  card!: any;
  data$!: Observable<any>;

  constructor() {
  }

  ngOnInit(): void {
  }

}
