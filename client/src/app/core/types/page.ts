export interface Page<T> {
    content: T;
    empty: boolean;
    first: boolean;
    last: boolean;
    numberOfElements: number;
    size: number;
    totalElements: number;
    totalPages: number;
}
