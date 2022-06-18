import fs from 'fs';
import { parse } from 'fast-csv';
import { getClient } from '../utils/client';
import { Axios, AxiosInstance } from 'axios';

function pushData(client: AxiosInstance, data: any) {
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
    }
    client.post(`/stocks/import`, dto)
        .catch(err => console.error(`Cannot push data for ${data.symbol}.${data.exchange}`));
    console.log(dto);
}

export async function importStocks() {
    const client = await getClient();
    return fs.createReadStream('./resources/stocks-temp.csv')
        .pipe(parse({headers: true}))
        .on('error', error => console.error(error))
        .on('data', row => pushData(client, row))
        .on('end', (rowCount: number) => console.log(`Pushed ${rowCount} stocks`));
}

importStocks();
