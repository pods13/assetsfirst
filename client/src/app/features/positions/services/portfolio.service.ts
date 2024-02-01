import {Injectable} from "@angular/core";
import {HttpClient} from "@angular/common/http";
import {PortfolioShortInfoDto} from "../types/portfolio-short-info.dto";

@Injectable()
export class PortfolioService {

    constructor(private http: HttpClient) {
    }

    getPortfolioShortInfo() {
        return this.http.get<PortfolioShortInfoDto>(`/portfolios/me`);
    }
}
