import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { TagDto } from '../../types/tag/tag.dto';
import { delay, Observable } from 'rxjs';
import { MatChipInputEvent } from '@angular/material/chips';
import { COMMA, ENTER } from '@angular/cdk/keycodes';
import { TagCategoryService } from '../../services/tag-category.service';
import { TagCategoryDto } from '../../types/tag/tag-category.dto';
import { MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-tag-categories-dialog',
  template: `
    <div mat-dialog-title class="dialog-title">
      <h2>
        Tag Categories
      </h2>
      <button mat-button class="add-category-btn" (click)="addCategory()">
        <mat-icon>add</mat-icon>
      </button>
    </div>

    <mat-dialog-content>
      <mat-card *ngFor="let category of categories; let i = index">
        <mat-card-title class="category-title">
          <h3>{{category.name}}</h3>
          <button mat-button class="delete-category-btn" (click)="deleteCategory(category)">
            <mat-icon>cancel</mat-icon>
          </button>
        </mat-card-title>

        <mat-form-field appearance="fill" class="category-tags-list">
          <mat-chip-list #categoriesTags aria-label="Tag selection">
            <mat-chip *ngFor="let tag of category.tags"
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
      </mat-card>
    </mat-dialog-content>
    <mat-dialog-actions>
      <button mat-button (click)="onOkayClick()">Okay</button>
    </mat-dialog-actions>
  `,
  styleUrls: ['./tag-categories-dialog.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush
})
export class TagCategoriesDialogComponent implements OnInit {
  readonly separatorKeysCodes = [ENTER, COMMA] as const;
  readonly newCategoryName = 'New Tag Category' as const;

  initialTagCategories!: any[];
  categories!: TagCategoryDto[];

  categoriesToDelete: number[] = [];

  constructor(public tagCategoryService: TagCategoryService,
              private cd: ChangeDetectorRef,
              private dialogRef: MatDialogRef<TagCategoriesDialogComponent>) {
  }

  ngOnInit(): void {
    this.tagCategoryService.getTagCategories().subscribe(categories => {
      this.initialTagCategories = categories;
      this.categories = JSON.parse(JSON.stringify(this.initialTagCategories));
      this.cd.detectChanges();
    });
  }

  removeTag(category: TagCategoryDto, tag: TagDto) {
    category.tags = [...category.tags.filter(t => t.id !== tag.id)];
    this.cd.detectChanges();
  }

  addTag(category: TagCategoryDto, event: MatChipInputEvent): void {
    const value = (event.value || '').trim();
    if (value) {
      category.tags = [...category.tags, {name: value} as TagDto];
      event.chipInput!.clear();
      this.cd.detectChanges();
    }
  }

  onOkayClick() {
    const newCategories = this.categories.filter(category => !category.id);
    const initialCategoryStateById = this.initialTagCategories.reduce((obj, item) => ({...obj, [item.id]: item}), {});
    const categoriesToUpdate = this.categories
      .filter(category => !!category.id && JSON.stringify(category.tags) !== JSON.stringify(initialCategoryStateById[category.id].tags));
    this.dialogRef.close({add: newCategories, update: categoriesToUpdate, delete: this.categoriesToDelete});
  }

  addCategory() {
    this.categories = [{id: null, name: this.generateNewCategoryName(), color: '#999', tags: []}, ...this.categories];
    this.cd.detectChanges();
  }

  generateNewCategoryName(): string {
    const latestNewCategory = this.categories.find(c => c.name.startsWith(this.newCategoryName));
    if (!latestNewCategory) {
      return this.newCategoryName;
    }
    let extraNameSuffix: string;
    if (latestNewCategory.name === this.newCategoryName) {
      extraNameSuffix = ' 2';
    } else {
      extraNameSuffix = ` ${+latestNewCategory.name[latestNewCategory.name.length - 1] + 1}`;
    }
    return this.newCategoryName + extraNameSuffix;
  }

  deleteCategory(category: TagCategoryDto) {
    if (category.id) {
      this.categoriesToDelete = [...this.categoriesToDelete, category.id];
    }
    this.categories = [...this.categories.filter(c => c.name !== category.name)];
    this.cd.detectChanges();
  }
}

export interface TagCategoriesDialogReturnType {
  add: TagCategoryDto[];
  update: TagCategoryDto[];
  delete: number[];
}
