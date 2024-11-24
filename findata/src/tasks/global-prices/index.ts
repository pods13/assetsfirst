#!/usr/bin/env node

import yahooFinance from 'yahoo-finance2';
import connection from '../../common/connection';
import {getInstruments} from '../../common/instrument.service';
import {Instrument} from "../../common/types/instrument";

const US_EXCHANGES = ['NYSE', 'NYSEARCA', 'NASDAQ'];

const getQuote = async (instrument: Instrument) => {
    const ticker = convertToYahooTicker(instrument.symbol, instrument.exchange);
    return yahooFinance.quoteSummary(ticker, {modules: ['price']})
        .catch(e => console.error(`Cannot find price data for ${ticker}: `, e))
        .then(quote => {
            if (!quote) {
                console.warn(`Got empty quote for ${ticker}`);
                return null;
            }
            const price = quote.price?.regularMarketPrice ?? quote.price?.regularMarketPreviousClose;
            if (!price) {
                console.warn(`Got empty price for ${ticker}`);
                return null;
            }
            return {
                instrument_id: instrument.id,
                datetime: new Date(),
                value: price
            };
        });
}

async function main() {
    const instruments = await getInstruments(connection, ['NYSE', 'NASDAQ', 'NYSEARCA', 'HK', 'XETRA']);

    const whenInstrumentPricesInserted = instruments.map(instrument => {
        return getQuote(instrument)
            .then(res => {
                if (res) {
                    return connection('instrument_price').insert(res)
                        .catch(e => console.error(e))
                        .then(() => console.log(`${instrument.symbol} inserted`));
                } else {
                    return Promise.resolve();
                }
            })
    });

    await Promise.allSettled(whenInstrumentPricesInserted)
        .catch(console.error)
        .finally(() => connection.destroy());
}

function convertToYahooTicker(symbol: string, exchange: string): string {
    if (US_EXCHANGES.includes(exchange)) {
        return `${symbol}`;
    } else if ('XETRA' === exchange) {
        return `${symbol}.DE`;
    } else if ('MCX' === exchange) {
        return `${symbol}.ME`;
    }

    return `${symbol}.${exchange}`;
}

main();
