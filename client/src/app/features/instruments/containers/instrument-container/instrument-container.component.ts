import {ChangeDetectionStrategy, Component, Input, OnInit} from '@angular/core';
import {InstrumentService} from '../../services/instrument.service';
import {Observable} from 'rxjs';
import {InstrumentDto} from '../../types/instrument.dto';

@Component({
    selector: 'app-symbol-container',
    template: `
    <div *ngIf="instrument$ | async as symbol">
      <h1>{{symbol.name}}</h1>
      <p>symbol price</p>
    </div>
  `,
    styleUrls: ['./instrument-container.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class InstrumentContainerComponent implements OnInit {

    @Input()
    identifier!: string;

    instrument$!: Observable<InstrumentDto>;

    constructor(private instrumentService: InstrumentService) {
    }

    ngOnInit(): void {
        this.instrument$ = this.instrumentService.findInstrumentByIdentifier(this.identifier);
    }

}
