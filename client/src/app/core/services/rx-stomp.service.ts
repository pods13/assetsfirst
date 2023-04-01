import { Injectable } from '@angular/core';
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';
import { HttpXsrfTokenExtractor } from '@angular/common/http';
import { environment } from '@env';

@Injectable()
export class RxStompService extends RxStomp {

  constructor(private tokenExtractor: HttpXsrfTokenExtractor) {
    super();
  }

  setConnectHeaders(): void {
    console.log('run setConnectHeaders')
    this.stompClient.connectHeaders = {'X-XSRF-TOKEN': `${this.tokenExtractor.getToken()}`};
  }
}

const debugFunc = (msg: string) => {
  console.log(new Date(), msg);
}

export const rxStompConfig: RxStompConfig = {
  brokerURL: environment.brokerUrl,

  heartbeatIncoming: 0,
  heartbeatOutgoing: 20000,

  connectionTimeout: 1000,
  reconnectDelay: 3000,

  beforeConnect: client => (client as RxStompService).setConnectHeaders(),

  debug: undefined,
};

export function rxStompServiceFactory(tokenExtractor: HttpXsrfTokenExtractor) {
  const rxStomp = new RxStompService(tokenExtractor);
  rxStomp.configure(rxStompConfig);
  return rxStomp;
}
