import * as playwright from 'playwright-chromium';
import { randomInteger } from '../utils/random-int';
import { CsvFormatterStream, format, parse } from 'fast-csv';
import fs from 'fs';
import { unlink } from 'fs/promises';
import modifiedData from './data/stock/modify.json';
import { InstrumentData } from '../common/types/instrument-data';

export async function collectStocks(country: string, exchanges: string[] = []) {
    if (!exchanges.length) {
        return await collectStocksByExchange(country, null);
    }
    const whenStocksCollected = exchanges.map(exchange => collectStocksByExchange(country, exchange));
    const filenames = await Promise.allSettled(whenStocksCollected);
    const mainStream = format({headers: true});
    const filename = composeFilename(country);
    mainStream.pipe(fs.createWriteStream(`./resources/stocks/${filename}`));
    filenames.filter(f => f.status === "fulfilled")
        .map(f => {
            const filename = (f as PromiseFulfilledResult<string>).value;
            const filePath = `./resources/stocks/${filename}`;
            return fs.createReadStream(filePath)
                .pipe(parse({headers: true}))
                .on('error', error => console.error(error))
                .on('data', row => mainStream.write(row))
                .on('end', () => unlink(filePath));
        })
}

async function collectStocksByExchange(country: string, exchange: string | null): Promise<string> {
    const csvStream = format({headers: true});
    const filename = composeFilename(exchange ?? country);
    csvStream.pipe(fs.createWriteStream(`./resources/stocks/${filename}`));

    const browser = await playwright.chromium.launch({
        headless: false, args: ['--single-process']
    });

    const context = await browser.newContext();

    let pageNum = 0;
    try {
        const page = await openStockScreenerPage(context);
        await selectCountry(page, country);
        await selectExchange(page, exchange);
        let nextButtonVisible = await isNextButtonVisible(page);
        while (pageNum === 0 || nextButtonVisible) {
            pageNum += 1;
            const pageData = await collectPageData(page);
            savePageData(csvStream, pageData);
            await closePopup(page);
            nextButtonVisible = await isNextButtonVisible(page);
            if (nextButtonVisible) {
                await selectNextPage(page, pageNum + 1);
                await page.waitForTimeout(randomInteger(3000, 10000));
            }
        }
    } catch (e) {
        console.error(`Number of the last saved page: ${pageNum}`)
        console.error(e);
    } finally {
        csvStream.end();
        await browser.close();
    }

    return filename;
}

function composeFilename(subject: string) {
    return `${subject.replaceAll(' ', '-').toLowerCase()}.csv`;
}

async function openStockScreenerPage(browserContext: playwright.BrowserContext): Promise<playwright.Page> {
    const page = await browserContext.newPage();
    await page.route('**/*', (route) => {
        // abort requests from known ad domains
        if (['cdc', 'yandex.ru', 'ad_status.js', 'tracker'].find(r => route.request().url().includes(r))) {
            route.abort();
        } else {
            route.continue();
        }
    });
    try {
        await page.goto('https://ru.investing.com/stock-screener/?sp=country::27|sector::a|industry::a|equityType::a|exchange::a%3Ename_trans;1', {waitUntil: 'domcontentloaded', timeout: 90_000});
        await page.waitForTimeout(3000);
        await closeTrustPopup(page);
        return page;
    } catch (error) {
        console.error(error);
    }


    throw new Error(`Cannot open page`)
}


async function adjustTableColumns(page: playwright.Page) {
    await page.click('#colSelectIcon_stock_screener');
    await page.click('input#SS_6');
    await page.click('input#SS_5');
    await page.click('input#SS_4');
    await page.click('#selectColumnsButton_stock_screener');
}

const SELECT_COUNTRY_PLACEHOLDER = 'Выбрать страну'
const COUNTRY_MAPPER: {[name: string]: string} = {
    'Russia': 'Россия',
    'Germany': 'Германия',
    'United States': 'США',
    'Hong Kong': 'Гонконг',
}
async function selectCountry(page: playwright.Page, country: string) {
    await page.click(`[placeholder="${SELECT_COUNTRY_PLACEHOLDER}"]`);
    await page.click(`#countriesUL > li:has-text("${COUNTRY_MAPPER[country]}")`);
    await page.waitForTimeout(randomInteger(4000, 6000));
}

const SELECT_EXCHANGE_PLACEHOLDER = 'Выбрать биржу'
const EXCHANGE_MAPPER: {[name: string]: string} = {
    'NYSE': 'Нью-Йорк'
}
async function selectExchange(page: playwright.Page, exchange: string | null): Promise<void> {
    if (!exchange) {
        return;
    }
    await page.click(`[placeholder="${SELECT_EXCHANGE_PLACEHOLDER}"]`);
    await page.click(`#exchangesUL > li:has-text("${EXCHANGE_MAPPER[exchange] ?? exchange}")`);
    await page.waitForTimeout(randomInteger(4000, 6000));
}

async function closeTrustPopup(page: playwright.Page) {
    const isPopupAppeared = await page.isVisible('div#PromoteSignUpPopUp');
    if (isPopupAppeared) {
        await page.click('i.largeBannerCloser');
    }
}

async function closePopup(page: playwright.Page) {
    const isPopupAppeared = await page.isVisible('.largeBannerCloser');
    if (isPopupAppeared) {
        await page.click('.largeBannerCloser');
    }
}

const NEXT_BUTTON_TEXT = 'След.';
async function isNextButtonVisible(page: playwright.Page) {
    return await page.isVisible(`.text_align_lang_base_2 > a:has-text("${NEXT_BUTTON_TEXT}")`);
}

async function collectPageData(page: playwright.Page): Promise<InstrumentData[]> {
    const parsedData: InstrumentData[] = await page.$$eval('#resultsTable > tbody tr', (equities) => {
        return equities.map(eq => {
            const name = eq.querySelector('td:nth-child(2)').textContent;
            const link = eq.querySelector('td:nth-child(2) a').getAttribute('href');
            const slug = link ? link.split('/').pop() : '';
            const symbol = eq.querySelector('td:nth-child(3)').textContent;
            const exchange = eq.querySelector('td:nth-child(4)').textContent;
            const sector = eq.querySelector('td:nth-child(5)').textContent;
            const industry = eq.querySelector('td:nth-child(6)').textContent;
            return {
                symbol, exchange, name, sector, industry, slug
            };
        });
    });

    return parsedData.map(eq => {
        const exchange = mapExchange(eq.exchange);
        const sector = mapSector(eq.sector);
        const data = modifiedData as { [key: string]: object; };
        const eqModifications = data[`${eq.symbol}.${exchange}`] ?? {};
        return {...eq, exchange, sector, ...eqModifications};
    });
}

function mapExchange(exchange: string): string {
    if (exchange === 'Гонконг') {
        return 'HK';
    } else if (exchange === 'Москва') {
        return 'MCX';
    } else if(exchange === 'Нью-Йорк') {
        return 'NYSE';
    }
    return exchange.toUpperCase();
}

function mapSector(sector: string) {
    if (sector === 'Нециклические компании') {
        return 'Потребительские товары повседневного спроса';
    } else if (sector === 'Циклические компании') {
        return 'Потребительские товары выборочного спроса';
    }
    return sector;
}

function savePageData(csv: CsvFormatterStream<any, any>, pageData: any[]) {
    pageData.forEach(data => csv.write(data));
}

async function selectNextPage(page: playwright.Page, pageToSelect: number) {
    return await page.click(`.text_align_lang_base_2 > a:has-text("${NEXT_BUTTON_TEXT}")`);
}
