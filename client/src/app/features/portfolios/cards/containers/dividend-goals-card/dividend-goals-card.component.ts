import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { CardContainer } from '../../types/card-container';
import { Observable } from 'rxjs';

@Component({
  selector: 'app-dividend-goals-card',
  template: `
    <p>
      dividend-goals-card works!
    </p>
  `,
  styleUrls: ['./dividend-goals-card.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class DividendGoalsCardComponent implements OnInit, CardContainer<any> {

  card!: any;
  data$!: Observable<any>;

  constructor() { }

  ngOnInit(): void {
  }

}
