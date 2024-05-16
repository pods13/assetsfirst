import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {InstrumentDto} from '../types/instrument.dto';

@Injectable()
export class InstrumentService {

    constructor(private http: HttpClient) {
    }

    findInstrumentByIdentifier(identifier: string) {
        return this.http.get<InstrumentDto>(`/instruments/${identifier}`);
    }
}
