import {
  ChangeDetectionStrategy,
  ChangeDetectorRef,
  Component,
  ElementRef,
  Inject,
  OnInit,
  ViewChild,
  ViewEncapsulation
} from '@angular/core';
import { TagCategoryService } from '@core/services/tag-category.service';
import { filter, map, mergeMap, Observable, startWith } from 'rxjs';
import { MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA } from '@angular/material/legacy-dialog';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatLegacyChipInputEvent as MatChipInputEvent } from '@angular/material/legacy-chips';
import { FormControl, NonNullableFormBuilder } from '@angular/forms';
import { TagWithCategoryDto } from '../../../../core/types/tag/tag.dto';
import { MatLegacyAutocompleteSelectedEvent as MatAutocompleteSelectedEvent } from '@angular/material/legacy-autocomplete';

@Component({
  selector: 'app-position-tags-dialog',
  template: `
    <h2 mat-dialog-title>
      {{ title }}<span>
      <button mat-icon-button class="conf-categories-btn"
              matTooltip="Configure Tag Categories" [mat-dialog-close]="{openTagCategoriesDialog: true}">
        <mat-icon>settings_suggest</mat-icon>
      </button>
    </span>
    </h2>
    <mat-dialog-content>
      <mat-form-field class="tag-chip-list" appearance="fill">
        <mat-label>Position Tags</mat-label>
        <mat-chip-list #chipList aria-label="Tag selection">
          <mat-chip *ngFor="let tag of selectedTags" (removed)="remove(tag)">
            {{tag.categoryName + '::' + tag.name}}
            <button matChipRemove>
              <mat-icon>cancel</mat-icon>
            </button>
          </mat-chip>
          <input placeholder="New tag..." #tagInput [formControl]="tagCtrl"
                 [matChipInputFor]="chipList"
                 [matAutocomplete]="auto"
                 [matChipInputSeparatorKeyCodes]="separatorKeysCodes"
                 (matChipInputTokenEnd)="add($event)">
        </mat-chip-list>
        <mat-autocomplete #auto="matAutocomplete" (optionSelected)="selected($event)">
          <mat-option *ngFor="let tag of filteredTags$ | async" [value]="tag">
            {{tag.categoryName + '::' + tag.name}}
          </mat-option>
        </mat-autocomplete>
      </mat-form-field>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button [mat-dialog-close]="{selectedTags}">Okay</button>
    </mat-dialog-actions>
  `,
  styleUrls: ['./position-tags-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PositionTagsDialogComponent implements OnInit {

  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  title!: string;

  tagCtrl: FormControl<string>;
  selectedTags: TagWithCategoryDto[] = [];
  filteredTags$!: Observable<TagWithCategoryDto[]>;

  @ViewChild('tagInput') tagInput!: ElementRef<HTMLInputElement>;

  constructor(public tagCategoryService: TagCategoryService,
              @Inject(MAT_DIALOG_DATA) private data: PositionTagsDialogData,
              private cd: ChangeDetectorRef,
              private fb: NonNullableFormBuilder) {
    this.title = data.title;
    this.selectedTags = [...data.selectedTags];
    this.tagCtrl = this.fb.control<string>('');
  }

  ngOnInit(): void {
    this.filteredTags$ = this.tagCtrl.valueChanges.pipe(
      startWith(''),
      filter(value => typeof value === 'string'),
      mergeMap((tagName: string) => this.tagCategoryService.findTags(tagName)
        .pipe(map(page => page.content))
      ),
    )
  }

  add(event: MatChipInputEvent): void {
    const value = (event.value || '').trim();

    if (value) {
      //TODO implement auto creating inputed tags by adding them to tag category first
    }

    // Clear the input value
    event.chipInput!.clear();

    this.tagCtrl.setValue('');
  }

  remove(tag: TagWithCategoryDto): void {
    this.selectedTags = [...this.selectedTags.filter(t => t.id !== tag.id)];
    this.cd.detectChanges();
  }

  selected(event: MatAutocompleteSelectedEvent): void {
    this.tagInput.nativeElement.value = '';
    this.tagCtrl.setValue('');
    const selectedTag = event.option.value;
    this.selectedTags = [...this.selectedTags, selectedTag];
    this.cd.detectChanges();
  }
}

export interface PositionTagsDialogData {
  title: string;
  selectedTags: TagWithCategoryDto[];
}

export interface PositionTagsDialogReturnType {
  selectedTags?: TagWithCategoryDto[];
  openTagCategoriesDialog?: boolean;
}
