import { TagDto } from './tag.dto';

export interface TagCategoryDto {
  id: number;
  name: string;
  color: string;
  tags: TagDto[];
}
