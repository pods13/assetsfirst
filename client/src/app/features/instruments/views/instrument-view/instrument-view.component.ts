import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-symbol-view',
  template: `
    <app-symbol-container [identifier]="identifier"></app-symbol-container>
  `,
  styleUrls: ['./instrument-view.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class InstrumentViewComponent implements OnInit {

  identifier!: string;

  constructor(route: ActivatedRoute) {
    route.params.subscribe((params) => {
      this.identifier = params["identifier"];
    });
  }

  ngOnInit(): void {
    console.log(this.identifier)
  }

}
