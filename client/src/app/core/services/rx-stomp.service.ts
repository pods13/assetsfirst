import { Injectable, isDevMode } from '@angular/core';
import { RxStomp, RxStompConfig } from '@stomp/rx-stomp';
import { HttpXsrfTokenExtractor } from '@angular/common/http';

@Injectable()
export class RxStompService extends RxStomp {

}

const debugFunc = (msg: string) => {
  console.log(new Date(), msg);
}

export const rxStompConfig: RxStompConfig = {
  brokerURL: 'ws://localhost:8080/live',

  heartbeatIncoming: 0,
  heartbeatOutgoing: 20000,

  connectionTimeout: 1000,
  reconnectDelay: 3000,

  debug: isDevMode() ? debugFunc : undefined,
};

export function rxStompServiceFactory(tokenExtractor: HttpXsrfTokenExtractor) {
  const rxStomp = new RxStompService();
  rxStomp.configure({...rxStompConfig, connectHeaders: {'X-XSRF-TOKEN': `${tokenExtractor.getToken()}`}});
  rxStomp.activate();
  return rxStomp;
}
