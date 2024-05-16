import {HttpParams} from '@angular/common/http';
import {Pageable} from '../types/pageable';

export function toHttpParams(pageable: Pageable): HttpParams {
    const {sorts, ...rest} = pageable;
    const sortParams = sorts ? sorts.map(sort => `${sort.prop},${sort.dir}`) : [];
    const fromObject = {
        ...rest,
        ...{sort: sortParams}
    }
    return new HttpParams({fromObject});
}
