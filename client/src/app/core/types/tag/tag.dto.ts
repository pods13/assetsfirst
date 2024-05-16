export interface TagDto {
    id: number;
    name: string;
}

export interface TagWithCategoryDto extends TagDto {
    categoryId: number;
    categoryName: string;
}
