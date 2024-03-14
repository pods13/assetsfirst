import path from 'path';
import fs from 'fs';
import { parse } from 'fast-csv';
import { RESOURCE_DIR, STOCKS_DIR } from './constants';
import { StockData } from './types/stock-data';
import { Knex } from 'knex';

export async function getStocksByCountry(country: string): Promise<StockData[]> {
    const filePath = path.join(RESOURCE_DIR, STOCKS_DIR, country.replace(' ', '-').toLowerCase() + '.csv');
    const result: StockData[] = [];
    return new Promise((resolve, reject) => {
        fs.createReadStream(filePath)
            .pipe(parse({headers: true}))
            .on('error', error => reject(error))
            .on('data', row => {
                if (filterData(row)) {
                    result.push(row)
                }
            })
            .on('end', (rowCount: number) => resolve(result));
    });
}

function filterData(data: StockData) {
    if (!data.sector && !data.industry) {
        return false;
    }
    return (data.slug.indexOf('?cid') === -1 && ['MCX', 'HK', 'NASDAQ', 'NYSE'].includes(data.exchange))
        || ('XETRA' === data.exchange && (data.slug.indexOf('?cid') === -1 || data.slug.indexOf('-ag') !== -1));
}

export async function getInstruments(connection: Knex, exchanges: string[], inAnyPortfolio = true): Promise<{ id: number; symbol: string; exchange: string; }[]> {
    return connection.raw(`select i.id, i.symbol as symbol, i.exchange_code as exchange
                           from instrument i
                           where i.exchange_code in (?)
                             and (? is false or
                                  i.id in
                                  (select distinct pos.instrument_id from portfolio_position pos))`, [exchanges, inAnyPortfolio])
        .then(res => res[0]);
}
