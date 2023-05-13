import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { TagDto } from '../../types/tag/tag.dto';
import { delay, Observable } from 'rxjs';
import { MatChipInputEvent } from '@angular/material/chips';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { TagCategoryService } from '../../services/tag-category.service';
import { TagCategoryDto } from '../../types/tag/tag-category.dto';

@Component({
  selector: 'app-tag-categories-dialog',
  template: `
    <h2 mat-dialog-title>
      Tag Categories
    </h2>
    <mat-dialog-content *ngIf="!loading; else showSpinner">
      <mat-card *ngFor="let category of categories$ | async; let i = index">
        <mat-card-title>{{category.name}}</mat-card-title>
        <mat-form-field appearance="fill" class="category-tags-list">
          <mat-chip-list #categoriesTags aria-label="Tag selection">
            <mat-chip *ngFor="let tag of category.tags"
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
      </mat-card>
    </mat-dialog-content>
    <ng-template #showSpinner>
      <div class="spinner-wrapper">
        <mat-spinner [color]="'accent'"></mat-spinner>
      </div>
    </ng-template>
    <mat-dialog-actions>
      <button mat-button [mat-dialog-close]="true">Okay</button>
    </mat-dialog-actions>
  `,
  styleUrls: ['./tag-categories-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TagCategoriesDialogComponent implements OnInit {
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  loading = false;

  categories$ = this.tagCategoryService.getTagCategories();

  constructor(public tagCategoryService: TagCategoryService,
              private cd: ChangeDetectorRef) {
  }

  ngOnInit(): void {
  }


  removeTag(categoryId: number, tag: TagDto) {
    this.loading = true;
    this.tagCategoryService.deleteCategoryTag(categoryId, tag.id)
      .pipe(delay(200))
      .subscribe(() => {
        this.categories$ = this.tagCategoryService.getTagCategories();
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
          this.categories$ = this.tagCategoryService.getTagCategories();
          this.loading = false;
          this.cd.detectChanges();
        });
    }
  }
}
