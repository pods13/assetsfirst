import {TagDto} from './tag.dto';

export interface TagCategoryDto {
    id: number | null;
    name: string;
    color: string;
    tags: TagDto[];
}
