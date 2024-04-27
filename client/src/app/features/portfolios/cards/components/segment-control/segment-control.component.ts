import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  EventEmitter, Input,
  OnInit,
  Output,
  ViewChild
} from '@angular/core';
import { FormControl, FormGroup, NonNullableFormBuilder, Validators } from '@angular/forms';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { TagWithCategoryDto } from '@core/types/tag/tag.dto';
import { combineLatest, filter, map, mergeMap, Observable, pairwise, startWith } from 'rxjs';
import { MatLegacyAutocompleteSelectedEvent as MatAutocompleteSelectedEvent } from '@angular/material/legacy-autocomplete';
import { TagCategoryService } from '@core/services/tag-category.service';
import { TagCategoryDto } from '@core/types/tag/tag-category.dto';
import { CustomSegment } from '../../types/in/asset-allocation-card';

@Component({
  selector: 'app-segment-control',
  template: `
    <mat-expansion-panel class="segment-control-panel">
      <mat-expansion-panel-header>
        <mat-panel-title>
          {{form.get('name')?.value}}
        </mat-panel-title>
        <mat-panel-description>
        </mat-panel-description>
      </mat-expansion-panel-header>
      <form [formGroup]="form">
        <mat-form-field appearance="fill" class="segment-name">
          <input matInput type="text" id="name" placeholder="Name" formControlName="name" autofocus>
        </mat-form-field>
        <mat-form-field class="segment-tags-list" appearance="fill">
          <mat-label>Position Tags</mat-label>
          <mat-chip-list #chipList aria-label="Tag selection">
            <mat-chip *ngFor="let tag of selectedTags" (removed)="removeTag(tag)">
              {{tag.categoryName + '::' + tag.name}}
              <button matChipRemove>
                <mat-icon>cancel</mat-icon>
              </button>
            </mat-chip>
            <input placeholder="Select tag..." #tagInput [formControl]="tagCtrl"
                   [matChipInputFor]="chipList"
                   [matAutocomplete]="auto"
                   [matChipInputSeparatorKeyCodes]="separatorKeysCodes">
          </mat-chip-list>
          <mat-autocomplete #auto="matAutocomplete" (optionSelected)="selectTag($event)">
            <mat-option *ngFor="let tag of filteredTags$ | async" [value]="tag">
              {{tag.categoryName + '::' + tag.name}}
            </mat-option>
          </mat-autocomplete>
        </mat-form-field>
      </form>
      <mat-action-row>
        <button mat-button color="warn" (click)="deleteSegment()">Delete</button>
      </mat-action-row>
    </mat-expansion-panel>
  `,
  styleUrls: ['./segment-control.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class SegmentControlComponent implements OnInit {
  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  @Input()
  segment!: CustomSegment;

  @Output()
  segmentDeleted = new EventEmitter<SegmentDeletedEvent>();
  @Output()
  segmentChanged = new EventEmitter<SegmentChangedEvent>();

  tagCtrl!: FormControl<string>;
  filteredTags$!: Observable<TagWithCategoryDto[]>;

  @ViewChild('tagInput') tagInput!: ElementRef<HTMLInputElement>;

  form!: FormGroup;

  constructor(private fb: NonNullableFormBuilder,
              public tagCategoryService: TagCategoryService,
              private cd: ChangeDetectorRef) {

  }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: [this.segment.name, Validators.required],
      selectedTags: this.fb.control<TagWithCategoryDto[]>(this.segment.tags),
    });
    this.tagCtrl = this.fb.control<string>('');

    this.filteredTags$ = this.tagCtrl.valueChanges.pipe(
      startWith(''),
      filter(value => typeof value === 'string'),
      mergeMap((tagName: string) => this.tagCategoryService.findTags(tagName)
        .pipe(map(page => page.content))
      ),
    );

    this.form.get('name')!.valueChanges.pipe(startWith(this.segment.name), pairwise())
      .subscribe(([prevName, currName]) => {
        this.segmentChanged.emit({name: prevName, segment: {name: currName, tags: this.selectedTags}});
      });
    this.form.get('selectedTags')!.valueChanges.pipe(startWith(this.segment.tags))
      .subscribe(selectedTags => {
        const name = this.form.get('name')?.value;
        this.segmentChanged.emit({name, segment: {name, tags: this.selectedTags}});
      })
  }

  get selectedTags(): TagWithCategoryDto[] {
    return this.form.get('selectedTags')?.value;
  }

  removeTag(tag: TagWithCategoryDto): void {
    this.form.patchValue({
      selectedTags: [...this.selectedTags.filter(t => t.id !== tag.id)]
    });
    this.cd.detectChanges();
  }

  selectTag(event: MatAutocompleteSelectedEvent): void {
    this.tagInput.nativeElement.value = '';
    this.tagCtrl.setValue('');
    const selectedTag = event.option.value;
    this.form.patchValue({
      selectedTags: [...this.selectedTags, selectedTag]
    });
    this.cd.detectChanges();
  }

  deleteSegment() {
    this.segmentDeleted.emit({initialName: this.segment.name, recentName: this.form.get('name')?.value});
  }
}

export interface SegmentChangedEvent {
  name: string;
  segment: CustomSegment;
}

export interface SegmentDeletedEvent {
  initialName: string;
  recentName: string;

}
