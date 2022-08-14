import { PageSort } from './page-sort';

export interface Pageable {
  size: number;
  page: number;
  sorts?: PageSort[];
}
