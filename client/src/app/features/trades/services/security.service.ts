import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';

@Injectable()
export class SecurityService {

  constructor(private http: HttpClient) {
  }

  searchSecuritiesByNameOrTicker(searchTerm: string) {
    return this.http.get<any[]>(`/securities?search=${searchTerm}`);
  }

}
