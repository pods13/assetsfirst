import {load} from 'cheerio';
import axios, { AxiosInstance } from "axios";
import { getClient } from '../../utils/client';
import { Page } from '../../utils/page';
import { addMonths } from '../../utils/add-months';

export async function fetchDividends() {
    const client = await getClient();
    const exchange = 'MCX';
    let page = 1;
    let last = false;
    do {
        const paginatedSecurities = await getTickersByExchange(client, exchange, page);
        await saveParsedDividends(client, paginatedSecurities.content);
        last = paginatedSecurities.last;
        page += 1;
    } while (!last);
}

async function getTickersByExchange(client: AxiosInstance, exchange: string, page: number) {
    const securitiesRes = await client.get<Page<any>>(`/exchanges/${exchange}/tickers?size=10&page=${page}&instrumentTypes=STOCK&inAnyPortfolio=true`);
    return securitiesRes.data;
}

async function saveParsedDividends(client: AxiosInstance, securities: any[]) {
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

fetchDividends();
