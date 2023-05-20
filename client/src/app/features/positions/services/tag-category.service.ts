import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { TagCategoryDto } from '../types/tag/tag-category.dto';
import { Observable } from 'rxjs';
import { TagDto, TagWithCategoryDto } from '../types/tag/tag.dto';
import { Page } from '../../../core/types/page';

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

  addTagToCategory(categoryId: number, tagName: string): Observable<TagDto> {
    return this.http.post<TagDto>(`/tag-categories/${categoryId}/tags`, null, {params: {tag: tagName}});
  }

  deleteCategoryTag(categoryId: number, tagId: number): Observable<void> {
    return this.http.delete<void>(`/tag-categories/${categoryId}/tags/${tagId}`);
  }

  findTags(tagNameSearchTerm: string) {
    return this.http.get<Page<TagWithCategoryDto[]>>(`/tag-categories/tags/search?searchTerm=${tagNameSearchTerm}`);
  }

  createTagCategory(category: TagCategoryDto) {
    return this.http.post<TagCategoryDto>(`/tag-categories`, category);
  }

  updateTagCategory(category: TagCategoryDto) {
    return this.http.patch<TagCategoryDto>(`/tag-categories/${category.id}`, category);
  }

  deleteTagCategory(categoryId: number) {
    return this.http.delete<void>(`/tag-categories/${categoryId}`);
  }
}
