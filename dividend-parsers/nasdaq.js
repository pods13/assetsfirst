import playwright from 'playwright-chromium';
import {getClient} from "./client.js";

async function main() {
    const client = await getClient();
    const securitiesRes = await client.get('/exchanges/US/tickers');
    const securities = securitiesRes.data;
    console.log(securities)
    const browser = await playwright.chromium.launch({
        headless: false, args: ['--single-process']
    });

    const context = await browser.newContext();
    const whenDividendsParsed = securities.map(({code}) => getDividendHistoryByTicker(context, code));
    try {
        const dividendData = await Promise.allSettled(whenDividendsParsed);
        const whenDividendsStored = dividendData.map((divs, index) => {
            if (divs.reason) {
                console.error(divs.reason);
                return Promise.resolve();
            }
            const {code, exchange} = securities[index];
            // console.log(divs.value[0])
            const dividends = divs.value.map(data => {
                return {
                    amount: data.amount ? data.amount.replace(/[^\d.]/g, "") : null,
                    declareDate: convertDate(data.declareDate),
                    recordDate: convertDate(data.recordDate),
                    payDate: convertDate(data.payDate)
                };
            });
            // console.log(code, exchange, dividends[0]);
            return client.post(`/dividends?ticker=${code}&exchange=${exchange}`, dividends);
        });
        await Promise.allSettled(whenDividendsStored);
    } catch (e) {
        console.error(e);
    } finally {
        await browser.close();
    }
}

function convertDate(dateStr) {
    //dateStr formatted as MM/dd/yyyy
    if (dateStr) {
        const dateParts = dateStr.split('/');
        return dateParts[2] + '-' + dateParts[0] + '-' + dateParts[1];
    }
    return null;
}

async function getDividendHistoryByTicker(browserContext, ticker) {
    const page = await browserContext.newPage();
    try {
        await page.goto(getPageUrl(ticker), {waitUntil: 'domcontentloaded'});
        await page.waitForTimeout(600);
        return await parseDividendHistory(page);
    } catch (error) {
        console.error(error);
    } finally {
        await page.close();
    }
    return [];
}

function getPageUrl(ticker) {
    return `https://www.nasdaq.com/market-activity/stocks/${ticker}/dividend-history`;
}

async function parseDividendHistory(page) {
    const selector = '.dividend-history__row.dividend-history__row--data'
    const elements = page.locator(selector);
    return await elements.evaluateAll((rows) => {
        return rows
            .map((el) => {
                const amount = el.querySelector('.dividend-history__cell--amount').innerText;
                const declareDate = el.querySelector('.dividend-history__cell--declarationDate').innerText;
                const recordDate = el.querySelector('.dividend-history__cell--recordDate').innerText;
                const payDate = el.querySelector('.dividend-history__cell--paymentDate').innerText;
                return {
                    amount,
                    declareDate,
                    recordDate,
                    payDate
                };
            });
    });
}

main();
