import {ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit} from '@angular/core';
import {COMMA, ENTER} from '@angular/cdk/keycodes';
import {TagCategoryService} from '@core/services/tag-category.service';
import {TagCategoryDto} from '@core/types/tag/tag-category.dto';
import {MatDialogRef} from '@angular/material/dialog';
import {CategoryChangedEvent} from '../tag-category-control/tag-category-control.component';

@Component({
    selector: 'app-tag-categories-dialog',
    template: `
    <div mat-dialog-title class="dialog-title">
      <h2>{{'Tag Categories'}}</h2>
      <button mat-button class="add-category-btn" (click)="addCategory()">
        <mat-icon>add</mat-icon>
      </button>
    </div>
    <mat-dialog-content>
      <mat-accordion>
        <app-tag-category-control *ngFor="let category of newCategories; let i = index" [category]="category"
                                  (categoryChanged)="handleCategoryChanged($event)"
                                  (categoryDeleted)="handleCategoryDeleted($event)">
        </app-tag-category-control>
        <app-tag-category-control *ngFor="let category of persistedCategories; let i = index" [category]="category"
                                  (categoryChanged)="handleCategoryChanged($event)"
                                  (categoryDeleted)="handleCategoryDeleted($event)">
        </app-tag-category-control>
      </mat-accordion>
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

    persistedCategories!: any[];
    newCategories!: any[];
    categoriesState!: TagCategoryDto[];

    categoriesToDelete: number[] = [];

    constructor(public tagCategoryService: TagCategoryService,
                private cd: ChangeDetectorRef,
                private dialogRef: MatDialogRef<TagCategoriesDialogComponent>) {
    }

    ngOnInit(): void {
        this.tagCategoryService.getTagCategories().subscribe(categories => {
            this.persistedCategories = categories;
            this.newCategories = [];
            this.categoriesState = JSON.parse(JSON.stringify(this.persistedCategories));
            this.cd.detectChanges();
        });
    }

    onOkayClick() {
        const newCategories = this.categoriesState.filter(category => !category.id);
        const initialCategoryStateById = this.persistedCategories.reduce((obj, item) => ({...obj, [item.id]: item}), {});
        const categoriesToUpdate = this.categoriesState
            .filter(category => !!category.id && JSON.stringify(category) !== JSON.stringify(initialCategoryStateById[category.id]));
        this.dialogRef.close({add: newCategories, update: categoriesToUpdate, delete: this.categoriesToDelete});
    }

    addCategory() {
        const newCategory = {id: null, name: this.generateNewCategoryName(), color: '#999', tags: []};
        this.newCategories = [newCategory, ...this.newCategories];
        this.categoriesState = [newCategory, ...this.categoriesState];
        this.cd.detectChanges();
    }

    generateNewCategoryName(): string {
        const latestNewCategory = this.categoriesState.find(c => c.name.startsWith(this.newCategoryName));
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

    handleCategoryChanged({name, category}: CategoryChangedEvent) {
        this.categoriesState = this.categoriesState.map(c => {
            if (c.name === name) {
                return category;
            }
            return c;
        });
    }

    handleCategoryDeleted(category: TagCategoryDto) {
        if (category.id) {
            this.categoriesToDelete = [...this.categoriesToDelete, category.id];
        }
        this.newCategories = [...this.newCategories.filter(c => c.name !== category.name)];
        this.persistedCategories = [...this.persistedCategories.filter(c => c.name !== category.name)];
        this.categoriesState = [...this.categoriesState.filter(c => c.name !== category.name)];
        this.cd.detectChanges();
    }
}

export interface TagCategoriesDialogReturnType {
    add: TagCategoryDto[];
    update: TagCategoryDto[];
    delete: number[];
}
