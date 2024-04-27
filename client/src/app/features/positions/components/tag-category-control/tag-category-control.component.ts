import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { TagCategoryDto } from '@core/types/tag/tag-category.dto';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { FormGroup, NonNullableFormBuilder, Validators } from '@angular/forms';
import { TagDto } from '@core/types/tag/tag.dto';
import { MatLegacyChipInputEvent as MatChipInputEvent } from '@angular/material/legacy-chips';
import { pairwise, startWith } from 'rxjs';

@Component({
  selector: 'app-tag-category-control',
  template: `
    <mat-expansion-panel class="category-control-panel">
      <mat-expansion-panel-header>
        <mat-panel-title>
          {{form.get('name')?.value}}
        </mat-panel-title>
        <mat-panel-description>
        </mat-panel-description>
      </mat-expansion-panel-header>
      <form [formGroup]="form">
        <mat-form-field appearance="fill" class="category-name">
          <input matInput type="text" id="name" placeholder="Name" formControlName="name" autofocus>
        </mat-form-field>
        <mat-form-field appearance="fill" class="category-tags-list">
          <mat-chip-list #categoriesTags aria-label="Tag selection" multiple>
            <mat-chip *ngFor="let tag of tags"
                      (removed)="removeTag(category, tag)">
              {{tag.name}}
              <button matChipRemove>
                <mat-icon>cancel</mat-icon>
              </button>
            </mat-chip>
            <input placeholder="New Tag..."
                   [matChipInputFor]="categoriesTags"
                   [matChipInputSeparatorKeyCodes]="separatorKeysCodes"
                   [matChipInputAddOnBlur]="true"
                   (matChipInputTokenEnd)="addTag(category, $event)">
          </mat-chip-list>
        </mat-form-field>
      </form>
      <mat-action-row>
        <button mat-button color="warn" (click)="deleteCategory()">Delete</button>
      </mat-action-row>
    </mat-expansion-panel>
  `,
  styleUrls: ['./tag-category-control.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TagCategoryControlComponent implements OnInit {

  readonly separatorKeysCodes = [ENTER, COMMA] as const;

  @Input()
  category!: TagCategoryDto;

  @Output()
  categoryDeleted = new EventEmitter<TagCategoryDto>();
  @Output()
  categoryChanged = new EventEmitter<CategoryChangedEvent>();

  form!: FormGroup;
  tags!: TagDto[];

  constructor(private fb: NonNullableFormBuilder) {

  }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: [this.category.name, Validators.required],
    });
    this.tags = [...this.category.tags];

    this.form.get('name')?.valueChanges
      .pipe(startWith(this.category.name), pairwise())
      .subscribe(([prev, next]) => {
        this.categoryChanged.emit({name: prev, category: {...this.latestCategoryState, ...{name: next}}});
      });
  }

  removeTag(category: TagCategoryDto, tag: TagDto) {
    this.tags = [...this.tags.filter(t => t.id !== tag.id)];
    this.categoryChanged.emit({name: this.form.get('name')?.value, category: this.latestCategoryState});
  }

  addTag(category: TagCategoryDto, event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value && !this.tags.some(t => t.name === value)) {
      this.tags = [...this.tags, {name: value} as TagDto];
      event.chipInput!.clear();
      this.categoryChanged.emit({name: this.form.get('name')?.value, category: this.latestCategoryState});
    }
  }

  deleteCategory() {
    this.categoryDeleted.emit(this.latestCategoryState);
  }

  get latestCategoryState(): TagCategoryDto {
    return Object.assign({}, this.category, this.form.value, {tags: this.tags});
  }
}

export interface CategoryChangedEvent {
  name: string;
  category: TagCategoryDto;
}
