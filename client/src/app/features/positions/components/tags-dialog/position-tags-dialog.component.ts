import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { TagCategoryService } from '../../services/tag-category.service';
import { map } from 'rxjs';
import { TagDto } from '../../types/tag/tag.dto';
import { SelectedTagDto } from '../../types/tag/selected-tag.dto';
import { MAT_DIALOG_DATA } from '@angular/material/dialog';

@Component({
  selector: 'app-position-tags-dialog',
  template: `
    <mat-dialog-content>
      <mat-chip-list>
        <mat-chip *ngFor="let selectedTag of selectedTags" [value]="selectedTag.name">
          {{selectedTag.name}}
        </mat-chip>
      </mat-chip-list>
      <mat-tab-group>
        <mat-tab *ngFor="let category of categories$ | async; let i = index" [label]="category.name">
          <ng-template matTabContent>
            <mat-form-field appearance="fill">
              <mat-chip-list #categoriesTags aria-label="Tag selection">
                <mat-chip *ngFor="let tag of category.tags$ | async" (click)="onCategoryTagSelected(category.id, tag)">
                  {{tag.name}}
                </mat-chip>
              </mat-chip-list>
            </mat-form-field>
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

  categories$ = this.tagCategoryService.getTagCategories().pipe(
    map(categories => {
      return categories.map(c => ({...c, tags$: this.tagCategoryService.getTagsByCategory(c.id)}))
    })
  );

  selectedTags: SelectedTagDto[] = [];


  constructor(public tagCategoryService: TagCategoryService,
              @Inject(MAT_DIALOG_DATA) private data: DialogData) {
    this.selectedTags = [...data.selectedTags];
  }

  ngOnInit(): void {
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
}

export interface DialogData {
  selectedTags: SelectedTagDto[];
}
