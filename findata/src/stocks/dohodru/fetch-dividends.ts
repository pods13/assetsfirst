import {load} from 'cheerio';
import axios from "axios";
import { getClient } from '../../utils/client';

export async function fetchDividends() {
    const client = await getClient();
    //TODO use &page= param to iterate over all tickers
    const securitiesRes = await client.get(`/exchanges/MCX/tickers?size=10`);
    const securities = securitiesRes.data?.content;
    console.log(securities);
    const whenDividendsParsed = securities.map((s: any) => getDividendHistoryByTicker(s.symbol));
    try {
        const dividendData = await Promise.allSettled(whenDividendsParsed);
        const whenDividendsStored = dividendData.map((divs, index) => {
            if (divs.status === 'rejected') {
                const res = divs as PromiseRejectedResult;
                console.error(`Cannot load dividends data for: `, res.reason.config.url);
                return Promise.resolve();
            }
            const {symbol, exchange} = securities[index];
            const dividends = (divs as PromiseFulfilledResult<any>).value.map((data: any) => {
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

            // console.log(symbol, exchange, declaredDividends.length);
            return client.post(`/dividends?ticker=${symbol}&exchange=${exchange}`, dividends);
        });
        await Promise.allSettled(whenDividendsStored);
    } catch (e) {
        console.error(e);
    }
}

async function getDividendHistoryByTicker(symbol: string) {
    const res = await axios.get(`https://www.dohod.ru/ik/analytics/dividend/${symbol.toLowerCase()}`);
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

function addMonths(date: string | null, months: number) {
    if (!date) {
        return;
    }
    const res = new Date(date);
    const d = res.getDate();
    res.setMonth(res.getMonth() + +months);
    if (res.getDate() !== d) {
        res.setDate(0);
    }
    return res;
}

fetchDividends();
