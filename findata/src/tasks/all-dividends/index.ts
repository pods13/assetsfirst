#!/usr/bin/env node

import { getInstruments } from '../../common/instrument.service';
import connection from '../../common/connection';
import fsPromises from 'fs/promises';
import path from 'path';
import fs from 'fs';
import { parse } from 'fast-csv';
import axios from 'axios';
import { load } from 'cheerio';
import { addMonths } from '../../utils/add-months';
import { getClient } from '../../utils/client';

const tickerBySlug: { [key: string]: string } = {};

async function main() {
    const resourceFolderPath = './resources/stocks';
    const filenames = await fsPromises.readdir(resourceFolderPath);
    const whenFilesRead = filenames
        .map(f => fillTickerCacheFromFile(path.join(resourceFolderPath, f)));
    await Promise.all(whenFilesRead);
    const instruments = await getInstruments(connection, ['NYSE', 'NASDAQ', 'NYSEARCA', 'HK', 'XETRA']);
    const client = await getClient();

    const whenDividendsSaved = instruments.map(instrument => {
        const slug = tickerBySlug[`${instrument.symbol}.${instrument.exchange}`];
        if (!slug) {
            console.error(`Cannot create slug for instrument  ${instrument.symbol + ':' + instrument.exchange}`);
            return Promise.resolve();
        }
        return parseRecentDividends(slug)
            .catch(e => {
                console.error(`Error during dividend data gathering for ${instrument.symbol + ':' + instrument.exchange}: ${e}`);
                throw e;
            })
            .then(dividends => {
                return transformToDividendDtos(dividends);
            })
            .then(res => {
                if (res) {
                    return client.post(`/dividends?symbol=${instrument.symbol}&exchange=${instrument.exchange}`, res)
                        .then(() => console.log(`Dividends were received for ${instrument.symbol + ':' + instrument.exchange}`));
                } else {
                    return Promise.resolve();
                }
            });
    });

    await Promise.allSettled(whenDividendsSaved)
        .catch(console.error)
        .finally(() => connection.destroy());
}

async function fillTickerCacheFromFile(path: string) {
    return new Promise<any>((resolve, reject) => {
        fs.createReadStream(path)
            .pipe(parse({headers: true}))
            .on('error', reject)
            .on('data', row => {
                const key = `${row.symbol}.${row.exchange}`;
                tickerBySlug[key] = row.slug;
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
    return parsedDividends.filter(d => !!d.amount).map(d => {
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

main();
