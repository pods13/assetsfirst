export interface TickerSymbol {
  symbol: string;
  exchange: string;
}

export function stringifyTickerSymbol(ts: TickerSymbol) {
  return `${ts.symbol}.${ts.exchange}`;
}
