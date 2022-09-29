import path from 'path';
import fs from 'fs';
import { parse } from 'fast-csv';
import { RESOURCE_DIR, STOCKS_DIR } from './constants';
import { StockData } from './types/stock-data';

export async function getStocksByCountry(country: string): Promise<StockData[]> {
    const filePath = path.join(RESOURCE_DIR, STOCKS_DIR, country.toLowerCase() + '.csv');
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
