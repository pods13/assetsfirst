import cheerio from 'cheerio';
import axios from "axios";
import {getClient} from "./client.js";

async function main() {
    const client = await getClient();
    const securitiesRes = await client.get('/exchanges/MCX/tickers');
    const securities = securitiesRes.data;
    console.log(securities);
    const whenDividendsParsed = securities.map(({symbol}) => getDividendHistoryByTicker(symbol));
    try {
        const dividendData = await Promise.allSettled(whenDividendsParsed);
        const whenDividendsStored = dividendData.map((divs, index) => {
            if (divs.reason) {
                console.error(divs.reason);
                return Promise.resolve();
            }
            const {symbol, exchange} = securities[index];
            const dividends = divs.value.map(data => {
                return {
                    amount: data.amount ? data.amount.replace(/[^\d.]/g, "") : null,
                    declareDate: convertDate(data.declareDate),
                    recordDate: convertDate(data.recordDate),
                    payDate: convertDate(data.payDate)
                };
            });

            //TODO persist forecasted dividends as well
            const declaredDividends = dividends.filter(div => !!div.declareDate);
            // console.log(symbol, exchange, declaredDividends.length);
            return client.post(`/dividends?ticker=${symbol}&exchange=${exchange}`, declaredDividends);
        });
        await Promise.allSettled(whenDividendsStored);
    } catch (e) {
        console.error(e);
    }
}

async function getDividendHistoryByTicker(symbol) {
    const res = await axios.get(`https://www.dohod.ru/ik/analytics/dividend/${symbol.toLowerCase()}`);
    const $ = cheerio.load(res.data);
    const dividendsTable = $(`p.table-title:contains('Все выплаты') + table`);
    const result = [];
    dividendsTable.find('tr').each((i, divDataEl) => {
        if (i === 0) {
            return;
        }
        const divData = {};
        cheerio.load(divDataEl)('td').each((index, el) => {
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

function parseDate(dateAsText) {
    if (!dateAsText || dateAsText.includes('n/a')) {
        return null;
    }
    return dateAsText.replace(/[\\n\s(прогноз)]/g, '');
}

function convertDate(dateStr) {
    //dateStr formatted as dd.MM.yyyy
    if (dateStr) {
        const dateParts = dateStr.split('.');
        return dateParts[2] + '-' + dateParts[1] + '-' + dateParts[0];
    }
    return null;
}

main();
