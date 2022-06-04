import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BrokerDto } from '../types/broker.dto';

@Injectable()
export class BrokerService {

  constructor(private http: HttpClient) {
  }

  getBrokers() {
    return this.http.get<BrokerDto[]>(`/brokers`);
  }

}
