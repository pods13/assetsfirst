export interface Ticker {
    symbol: string;
    exchange: string;
}

export function stringifyTicker(ticker: Ticker) {
    return `${ticker.symbol}.${ticker.exchange}`;
}
