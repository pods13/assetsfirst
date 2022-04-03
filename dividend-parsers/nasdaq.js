import playwright from 'playwright-chromium';
import {authenticate} from "./auth.js";
import fetch from "node-fetch";

async function main() {
    const secureHeaders = await authenticate();
    const securitiesRes = await fetch('http://localhost:8080/securities?securityTypes=ETF,STOCK', {headers: secureHeaders});
    const securities = JSON.parse(await securitiesRes.text());
    // console.log(securities);

    const browser = await playwright.chromium.launch({
        headless: false, args: ['--single-process']
    });

    const context = await browser.newContext();
    // await securities.forEach(async({ticker}) => {
    //     const divs = await getDividendHistoryByTicker(context, ticker);
    //     console.log({[ticker]: divs})
    // });
    const dividendsPromise = securities.map(({ticker}) => getDividendHistoryByTicker(context, ticker));
    try {
        const dividends = await Promise.allSettled(dividendsPromise);
        const divsByTicker = {};
        dividends.forEach((divs, index) => {
            if (divs.reason) {
                console.error(divs.reason);
                return;
            }
            const {ticker} = securities[index];
            divsByTicker[ticker] = divs.value;
        })
        console.log(divsByTicker);
        return divsByTicker;
    } catch (e) {
        console.error(e);
    } finally {
        await browser.close();
    }
}

async function getDividendHistoryByTicker(browserContext, ticker) {
    const page = await browserContext.newPage();
    try {
        await page.goto(getPageUrl(ticker), {waitUntil: 'domcontentloaded'});
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
