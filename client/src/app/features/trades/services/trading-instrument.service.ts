import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {InstrumentDto} from '../types/instrument.dto';

@Injectable()
export class TradingInstrumentService {

    constructor(private http: HttpClient) {
    }

    searchInstrumentsByNameOrTicker(searchTerm: string) {
        return this.http.get<InstrumentDto[]>(`/instruments?search=${searchTerm}`);
    }

}
