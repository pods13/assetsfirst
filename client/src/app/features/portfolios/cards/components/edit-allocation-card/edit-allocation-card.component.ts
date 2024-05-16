import {ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit} from '@angular/core';
import {TagCategoryService} from '@core/services/tag-category.service';
import {SegmentChangedEvent, SegmentDeletedEvent} from '../segment-control/segment-control.component';
import {AssetAllocationCard, CustomSegment} from '../../types/in/asset-allocation-card';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
    selector: 'app-edit-allocation-card',
    template: `
    <h1 mat-dialog-title>Edit Allocation Card</h1>
    <div mat-dialog-content>
      <h2>Configure custom allocation type</h2>
      <div class="segments">
        <div class="segments-header">
          <h2>{{'Segments'}}</h2>
          <button mat-button class="add-segment-btn" (click)="addSegment()">
            <mat-icon>add</mat-icon>
          </button>
        </div>
        <mat-accordion>
          <app-segment-control *ngFor="let segment of segments" [segment]="segment"
                               (segmentChanged)="handleSegmentChanged($event)"
                               (segmentDeleted)="handleSegmentDeleted($event)">
          </app-segment-control>
        </mat-accordion>
      </div>
    </div>
    <div mat-dialog-actions>
      <button mat-button (click)="handleOkClick()">Ok</button>
      <button mat-button [mat-dialog-close]="null">Cancel</button>
    </div>
  `,
    styleUrls: ['./edit-allocation-card.component.scss'],
    changeDetection: ChangeDetectionStrategy.OnPush
})
export class EditAllocationCardComponent implements OnInit {

    segments!: CustomSegment[];

    segmentsState!: CustomSegment[];

    constructor(public tagCategoryService: TagCategoryService,
                private cd: ChangeDetectorRef,
                @Inject(MAT_DIALOG_DATA) public data: { title: string; card: AssetAllocationCard },
                public dialogRef: MatDialogRef<EditAllocationCardComponent>) {
        this.segments = data.card?.customSegments ?? [];
        this.segmentsState = JSON.parse(JSON.stringify(this.segments));
    }

    ngOnInit(): void {
    }

    addSegment() {
        //TOOD generate segmentName
        const segmentName = 'Segment#';
        this.segments = [{name: segmentName, tags: []}, ...this.segments];
        this.segmentsState = [{name: segmentName, tags: []}, ...this.segmentsState];
    }

    handleSegmentChanged(event: SegmentChangedEvent) {
        this.segmentsState = this.segmentsState.map(s => {
            if (s.name === event.name) {
                return event.segment;
            }
            return s;
        });
    }

    handleOkClick() {
        this.dialogRef.close({customSegments: this.segmentsState});
    }

    handleSegmentDeleted(event: SegmentDeletedEvent) {
        this.segments = this.segments.filter(s => s.name !== event.initialName);
        this.segmentsState = this.segmentsState.filter(s => s.name !== event.recentName);
    }
}
