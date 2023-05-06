import { ChangeDetectionStrategy, ChangeDetectorRef, Component, Inject, OnInit } from '@angular/core';
import { TagCategoryService } from '../../services/tag-category.service';
import { delay, map, Observable } from 'rxjs';
import { TagDto } from '../../types/tag/tag.dto';
import { SelectedTagDto } from '../../types/tag/selected-tag.dto';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { MatChipInputEvent } from '@angular/material/chips';
import { TagCategoryDto } from '../../types/tag/tag-category.dto';

@Component({
  selector: 'app-position-tags-dialog',
  template: `
    <mat-dialog-content>
      <mat-chip-list>
        <mat-chip *ngFor="let selectedTag of selectedTags" [value]="selectedTag.name">
          {{selectedTag.name}}
        </mat-chip>
      </mat-chip-list>
      <mat-tab-group (selectedTabChange)="onSelectedTabChanged($event)">
        <mat-tab *ngFor="let category of categories; let i = index" [label]="category.name">
          <ng-template matTabContent>
            <mat-form-field appearance="fill" class="category-tags-list" *ngIf="!loading; else showSpinner">
              <mat-chip-list #categoriesTags aria-label="Tag selection">
                <mat-chip *ngFor="let tag of tags$ | async"
                          (click)="onCategoryTagSelected(category.id, tag)"
                          (removed)="removeTag(category.id, tag)">
                  {{tag.name}}
                  <button matChipRemove>
                    <mat-icon>cancel</mat-icon>
                  </button>
                </mat-chip>
                <input placeholder="New Tag..."
                       [matChipInputFor]="categoriesTags"
                       [matChipInputSeparatorKeyCodes]="separatorKeysCodes"
                       [matChipInputAddOnBlur]="true"
                       (matChipInputTokenEnd)="addTag(category.id, $event)">
              </mat-chip-list>
            </mat-form-field>
            <ng-template #showSpinner>
              <div class="spinner-wrapper">
                <mat-spinner [color]="'accent'"></mat-spinner>
              </div>
            </ng-template>
          </ng-template>
        </mat-tab>
      </mat-tab-group>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button [mat-dialog-close]="selectedTags">Okay</button>
    </mat-dialog-actions>
  `,
  styleUrls: ['./position-tags-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class PositionTagsDialogComponent implements OnInit {

  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  loading = true;

  categories!: TagCategoryDto[];
  tags$!: Observable<TagDto[]>;

  selectedTags: SelectedTagDto[] = [];


  constructor(public tagCategoryService: TagCategoryService,
              @Inject(MAT_DIALOG_DATA) private data: DialogData,
              private cd: ChangeDetectorRef) {
    this.selectedTags = [...data.selectedTags];
  }

  ngOnInit(): void {
    this.tagCategoryService.getTagCategories()
      .subscribe(categories => {
        this.categories = categories;
        this.cd.detectChanges();
      });
  }

  onCategoryTagSelected(categoryId: number, tag: TagDto) {
    const selectedTagIds = new Set(this.selectedTags.map(t => t.id));
    if (selectedTagIds.has(tag.id)) {
      return;
    }
    this.selectedTags = [...this.selectedTags.filter(t => t.categoryId !== categoryId), {
      id: tag.id,
      categoryId,
      name: tag.name
    }];
  }

  removeTag(categoryId: number, tag: TagDto) {
    this.loading = true;
    this.tagCategoryService.deleteCategoryTag(categoryId, tag.id)
      .pipe(delay(200))
      .subscribe(() => {
        this.tags$ = this.tagCategoryService.getTagsByCategory(categoryId);
        this.loading = false;
        this.cd.detectChanges();
      });
  }

  addTag(categoryId: number, event: MatChipInputEvent): void {
    this.loading = true;
    const value = (event.value || '').trim();

    if (value) {
      this.tagCategoryService.addTagToCategory(categoryId, value)
        .pipe(delay(200))
        .subscribe(() => {
          event.chipInput!.clear();
          this.tags$ = this.tagCategoryService.getTagsByCategory(categoryId);
          this.loading = false;
          this.cd.detectChanges();
        });
    }

  }

  onSelectedTabChanged(event: any) {
    const categoryId = this.categories[event.index]?.id;
    this.tags$ = this.tagCategoryService.getTagsByCategory(categoryId);
    this.loading = false;
  }
}

export interface DialogData {
  selectedTags: SelectedTagDto[];
}
