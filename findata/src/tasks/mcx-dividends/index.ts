#!/usr/bin/env node

import connection from '../../common/connection';
import { getInstruments } from '../../common/instrument.service';
import axios from 'axios';
import { addMonths } from '../../utils/add-months';
import { load } from 'cheerio';
import { getClient } from '../../utils/client';

async function main() {
    const instruments = await getInstruments(connection, ['MCX'], false);
    const client = await getClient();

    const whenDividendsSaved = instruments.map(instrument => {
        return getDividendHistoryByTicker(instrument.symbol)
            .catch(e => {
                console.error(`Error during dividend data gathering for ${instrument.symbol + ':' + instrument.exchange}`);
                throw e;
            })
            .then(dividends => {
                return dividends.map((data) => {
                    const declareDate = convertDate(data.declareDate);
                    const recordDate = convertDate(data.recordDate);
                    const payDate = declareDate ? addMonths(recordDate, 1) : null;
                    return {
                        amount: data.amount ? data.amount.replace(/[^\d.]/g, "") : null,
                        declareDate,
                        recordDate,
                        payDate
                    };
                });
            })
            .then(res => {
                if (res) {
                    return client.post(`/dividends?ticker=${instrument.symbol}&exchange=${instrument.exchange}`, res)
                        .then(() => console.log(`Dividends were received for ${instrument.symbol + ':' + instrument.exchange}`));
                } else {
                    return Promise.resolve();
                }
            })
    });


    await Promise.allSettled(whenDividendsSaved)
        .catch(console.error)
        .finally(() => connection.destroy());
}

async function getDividendHistoryByTicker(symbol: string) {
    const sym = symbol.toLowerCase().replaceAll('_', '');
    const res = await axios.get(`https://www.dohod.ru/ik/analytics/dividend/${sym}`);
    const $ = load(res.data);
    const dividendsTable = $(`p.table-title:contains('Все выплаты') + table`);
    const result: any[] = [];
    dividendsTable.find('tr').each((i: any, divDataEl: any) => {
        if (i === 0) {
            return;
        }
        const divData: any = {};
        load(divDataEl)('td').each((index: any, el: any) => {
            if (index === 0) {
                divData['declareDate'] = parseDate($(el).text());
            } else if (index === 1) {
                divData['recordDate'] = parseDate($(el).text());
            } else if (index === 3) {
                divData['amount'] = $(el).text();
            }
        });
        result.push(divData);
    });
    return result;
}

function parseDate(dateAsText: string) {
    if (!dateAsText || dateAsText.includes('n/a')) {
        return null;
    }
    return dateAsText.replace(/[\\n\s(прогноз)]/g, '');
}

function convertDate(dateStr: string) {
    //dateStr formatted as dd.MM.yyyy
    if (dateStr) {
        const dateParts = dateStr.split('.');
        return dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0];
    }
    return null;
}

main();
