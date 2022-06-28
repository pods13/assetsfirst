import { parse } from 'fast-csv';
import fs from 'fs';
import fsPromises from 'fs/promises';
import path from 'path';
import axios from 'axios';
import { load } from 'cheerio';
import { addMonths } from '../../utils/add-months';

const tickerSymbolBySlug: { [key: string]: string } = {};

export async function fetchDividends() {
    //TODO fetch stock dividends
    const resourceFolderPath = './resources/stocks';
    const filenames = await fsPromises.readdir(resourceFolderPath);
    const whenFilesRead = filenames.map(f => fillTickerSymbolCacheFromFile(path.join(resourceFolderPath, f)));
    await Promise.all(whenFilesRead);
    const parsedDividends = await parseRecentDividends(tickerSymbolBySlug['GAZP.MCX']);
    const dividendsToSave = transformToDividendDtos(parsedDividends);
    console.log(dividendsToSave);
    // console.log(await parseDividendHistory(tickerSymbolBySlug['YNDX.MCX']));


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
    const res = await axios.get(`https://www.investing.com/equities/${slug}-dividends`);
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

interface ParsedDividend {
    exDivDate: number;
    payDate: number | null;
    amount: number;
}

function transformToDividendDtos(parsedDividends: ParsedDividend[]) {
    return parsedDividends.map(d => {
        const oneDayInMs = 3600 * 1000 * 24;
        const recordDate = new Date(d.exDivDate + oneDayInMs);
        const declareDate = addMonths(recordDate, -1);

        const payDateTimestamp = d.payDate ?? addMonths(recordDate, 1)?.getTime();
        const payDate = payDateTimestamp ? new Date(payDateTimestamp) : null;
        return {
            amount: d.amount,
            declareDate,
            recordDate,
            payDate
        }
    })
}

fetchDividends();
