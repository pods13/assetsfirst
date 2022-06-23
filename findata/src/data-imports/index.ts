import fs from 'fs';
import { parse } from 'fast-csv';
import { getClient } from '../utils/client';
import { AxiosInstance } from 'axios';

function pushData(client: AxiosInstance, data: any) {
    if (!filterData(data)) {
        return;
    }
    const dto = {
        identifier: {
            symbol: data.symbol,
            exchange: data.exchange
        },
        company: {
            name: data.name,
            sector: data.sector,
            industry: data.industry,
        }
    };
    // console.log(dto);
    client.put(`/stocks/import`, dto)
        .catch(err => console.error(`Cannot push data for ${data.symbol}.${data.exchange}`));
}

function filterData(data: any) {
    if (!data.sector && !data.industry) {
        return false;
    }
    return (data.slug.indexOf('?cid') === -1 && ['MCX', 'HK', 'NASDAQ', 'NYSE'].includes(data.exchange))
        || ('XETRA' === data.exchange && (data.slug.indexOf('?cid') === -1 || data.slug.indexOf('-ag') !== -1));
}

export async function importStocks(pathToFile: string) {
    const client = await getClient();
    return fs.createReadStream(pathToFile)
        .pipe(parse({headers: true}))
        .on('error', error => console.error(error))
        .on('data', row => pushData(client, row))
        .on('end', (rowCount: number) => console.log(`Pushed ${rowCount} stocks`));
}

// importStocks('./resources/stocks/russia.csv');
// importStocks('./resources/stocks/hong-kong.csv');
// importStocks('./resources/stocks/germany.csv');
// importStocks('./resources/stocks/united-states.csv');
importStocks('./resources/stocks-temp.csv');
