import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';

@Injectable()
export class CurrencyService {

    constructor(private http: HttpClient) {
    }

    getAvailableCurrencyCodes() {
        return this.http.get<string[]>('/currencies/codes')
    }
}
