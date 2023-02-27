import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TagCategoryDto } from '../types/tag/tag-category.dto';
import { Observable } from 'rxjs';
import { TagDto } from '../types/tag/tag.dto';

@Injectable()
export class TagCategoryService {

  constructor(private http: HttpClient) {
  }

  getTagCategories(): Observable<TagCategoryDto[]> {
    return this.http.get<TagCategoryDto[]>('/tag-categories');
  }

  getTagsByCategory(categoryId: number): Observable<TagDto[]> {
    return this.http.get<TagDto[]>(`/tag-categories/${categoryId}/tags`);
  }
}
