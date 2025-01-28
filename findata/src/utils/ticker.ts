import {Instrument} from "../common/types/instrument";

const US_EXCHANGES = ['NYSE', 'NYSEARCA', 'NASDAQ'];

export function convertToYahooTicker({symbol, exchange}: Instrument): string {
    if (US_EXCHANGES.includes(exchange)) {
        return `${symbol}`;
    } else if ('XETRA' === exchange) {
        return `${symbol}.DE`;
    } else if ('MCX' === exchange) {
        return `${symbol}.ME`;
    }

    return `${symbol}.${exchange}`;
}