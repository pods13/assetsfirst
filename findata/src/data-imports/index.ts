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
    client.post(`/stocks/import`, dto)
        .catch(err => console.error(`Cannot push data for ${data.symbol}.${data.exchange}`));
    console.log(dto);
}

function filterData(data: any) {
    const supportedExchanges = ['MCX', 'XETRA', 'HK'];
    return data.slug.indexOf('?cid') === -1 && supportedExchanges.includes(data.exchange);
}

export async function importStocks(pathToFile: string) {
    const client = await getClient();
    return fs.createReadStream(pathToFile)
        .pipe(parse({headers: true}))
        .on('error', error => console.error(error))
        .on('data', row => pushData(client, row))
        .on('end', (rowCount: number) => console.log(`Pushed ${rowCount} stocks`));
}

importStocks('./resources/stocks/russia.csv');
importStocks('./resources/stocks/hong-kong.csv');
