import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {PortfolioPositionDto} from '../types/portfolio-position,dto';
import {PortfolioPositionView} from '../types/portfolio-position.view';
import {Observable} from 'rxjs';

@Injectable()
export class PortfolioPositionService {

    constructor(private http: HttpClient) {
    }

    getPortfolioPositions() {
        return this.http.get<PortfolioPositionDto[]>(`/portfolio-positions`);
    }

    getPortfolioPositionsView(hideSold: boolean) {
        return this.http.get<PortfolioPositionView[]>(`/portfolio-positions/view?hideSold=${hideSold}`);
    }

    updatePositionTags(positionId: number, selectedTagIds: number[]): Observable<void> {
        return this.http.post<void>(`/portfolio-positions/${positionId}/tags`, selectedTagIds);
    }
}
