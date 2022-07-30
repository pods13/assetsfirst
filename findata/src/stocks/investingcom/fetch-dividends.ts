import { parse } from 'fast-csv';
import fs from 'fs';
import fsPromises from 'fs/promises';
import path from 'path';
import axios, { AxiosInstance } from 'axios';
import { load } from 'cheerio';
import { addMonths } from '../../utils/add-months';
import { getClient } from '../../utils/client';
import { Page } from '../../utils/page';

const tickerSymbolBySlug: { [key: string]: string } = {};

export async function fetchAllDividends() {
    const resourceFolderPath = './resources/stocks';
    const filenames = await fsPromises.readdir(resourceFolderPath);
    const whenFilesRead = filenames
        .map(f => fillTickerSymbolCacheFromFile(path.join(resourceFolderPath, f)));
    await Promise.all(whenFilesRead);

    const client = await getClient();
    let page = 1;
    let last = false;
    do {
        const paginatedTickers = await getTickers(client, page);
        console.log(paginatedTickers.content)
        const whenDividendsPushed = paginatedTickers.content.map((s: any) =>
            pushDividends(client, {
                symbol: s.symbol,
                exchange: s.exchange,
                slug: tickerSymbolBySlug[`${s.symbol}.${s.exchange}`]
            }));
        try {
            await Promise.allSettled(whenDividendsPushed);
        } catch (e) {
            console.error(`Cannot push dividends`);
        }
        last = paginatedTickers.last;
        page += 1;
    } while (!last);
}

async function getTickers(client: AxiosInstance, page: number) {
    const res = await client.get<Page<any>>(`/exchanges/tickers?size=10&page=${page}&instrumentTypes=STOCK`);
    return res.data;
}

async function pushDividends(client: AxiosInstance, el: any) {
    const parsedDividends = await parseRecentDividends(el.slug);
    const dividendsToSave = transformToDividendDtos(parsedDividends);
    // console.log(dividendsToSave);
    return await client.post(`/dividends?ticker=${el.symbol}&exchange=${el.exchange}`, dividendsToSave);
}

async function fillTickerSymbolCacheFromFile(path: string) {
    return new Promise<any>((resolve, reject) => {
        fs.createReadStream(path)
            .pipe(parse({headers: true}))
            .on('error', reject)
            .on('data', row => {
                const key = `${row.symbol}.${row.exchange}`;
                tickerSymbolBySlug[key] = row.slug;
            }).on('end', resolve);
    })
}

async function parseRecentDividends(slug: string): Promise<ParsedDividend[]> {
    const url = composeDividendPageUrl(slug);
    const res = await axios.get(url);
    const $ = load(res.data);
    const dividendsTable = $(`.dividendTbl`);
    if (!dividendsTable) {
        return [];
    }
    const result: ParsedDividend[] = [];
    dividendsTable.find('tr').each((i: any, divDataEl: any) => {
        if (i === 0) {
            return;
        }
        const divData: any = {};
        load(divDataEl)('td').each((index: any, el: any) => {
            if (index === 0) {
                divData['exDivDate'] = Number.parseInt($(el).data('value') as string) * 1000;
            } else if (index === 1) {
                divData['amount'] = Number.parseFloat($(el).text());
            } else if (index === 3) {
                const timestamp = Number.parseInt($(el).data('value') as string);
                divData['payDate'] = timestamp > 0 ? timestamp * 1000 : null;
            }
        });
        result.push(divData as ParsedDividend);
    });
    return result;
}

function composeDividendPageUrl(slug: string): string {
    const [path, query] = slug.split('?');
    const suffix = path + '-dividends' + (query ? `?${query}` : '');
    return `https://www.investing.com/equities/${suffix}`;
}

interface ParsedDividend {
    exDivDate: number;
    payDate: number | null;
    amount: number;
}

function transformToDividendDtos(parsedDividends: ParsedDividend[]) {
    return parsedDividends.map(d => {
        const oneDayInMs = 3600 * 1000 * 24;
        const recordDate = new Date(d.exDivDate + oneDayInMs);

        const currentYear = new Date().getFullYear();
        let declareDate = null;
        let payDate = null;
        if (recordDate.getFullYear() === currentYear) {
            payDate = d.payDate ? new Date(d.payDate) : null;
            declareDate = d.payDate ? addMonths(recordDate, -1) : null;
        } else {
            declareDate = addMonths(recordDate, -1);
            const payDateTimestamp = d.payDate ?? addMonths(recordDate, 1)?.getTime();
            payDate = payDateTimestamp ? new Date(payDateTimestamp) : null;
        }
        return {
            amount: d.amount,
            declareDate,
            recordDate,
            payDate
        }
    })
}

fetchAllDividends();
