import fs from 'fs';
import { parse } from 'fast-csv';
import { getClient } from '../utils/client';
import { AxiosError, AxiosInstance } from 'axios';
import fsPromises from 'fs/promises';
import path from 'path';
import { StockData } from '../common/types/stock-data';

function pushData(client: AxiosInstance, data: StockData) {
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
    retry(() => client.put(`/stocks/import`, dto), isRetryableError)
        .catch(err => console.error(`Cannot push data for ${data.symbol}.${data.exchange}`));
}

function filterData(data: StockData) {
    if (!data.sector && !data.industry) {
        return false;
    }
    return (data.slug.indexOf('?cid') === -1 && ['MCX', 'HK', 'NASDAQ', 'NYSE'].includes(data.exchange))
        || ('XETRA' === data.exchange && (data.slug.indexOf('?cid') === -1 || data.slug.indexOf('-ag') !== -1));
}

export async function importCountryStocks(pathToFile: string) {
    const client = await getClient();
    return fs.createReadStream(pathToFile)
        .pipe(parse({headers: true}))
        .on('error', error => console.error(error))
        .on('data', row => pushData(client, row))
        .on('end', (rowCount: number) => {});
}

async function retry(fn: Function, retryCondition: (err: AxiosError) => boolean, retries = 2, err?: any) {
    if (!retries) {
        return Promise.reject(err);
    }
    return fn().catch((err: AxiosError) => {
        if (retryCondition(err)) {
            return retry(fn, retryCondition, retries - 1, err);
        }
        return Promise.reject(err);
    })
}

function isRetryableError(error: AxiosError) {
    if (!error.config) {
        return false;
    }
    console.error(error);
    const errorMsg: string = (error.response?.data as any)?.message ?? '';
    const companyDuplicate = errorMsg.indexOf('uq_company_name') !== -1;
    const sectorDuplicate = errorMsg.indexOf('uq_sector_name') !== -1;
    const industryDuplicate = errorMsg.indexOf('uq_industry_name') !== -1;
    return companyDuplicate || sectorDuplicate || industryDuplicate;
}

async function importStocks(country: string) {
    const resourceFolderPath = './resources/stocks';
    const filenames = await fsPromises.readdir(resourceFolderPath);
    for (let filename of filenames) {
        try {
            if (filename.includes(country.replaceAll(' ', '-').toLowerCase())) {
                const filePath = path.join(resourceFolderPath, filename);
                await importCountryStocks(filePath);
            } else {
                console.log(`Skip import of ${filename}`);
            }
        } catch (e) {
            console.error(e);
        }
    }
}

const args = process.argv.slice(2);

const country = args[0];
importStocks(country);
