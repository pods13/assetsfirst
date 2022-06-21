import playwright from 'playwright-chromium';
import { randomInteger } from '../../utils/random-int';
import { CsvFormatterStream, format } from 'fast-csv';
import fs from 'fs';

export async function collectStocks(countryName: string) {
    const browser = await playwright.chromium.launch({
        headless: false, args: ['--single-process']
    });

    const csvStream = format({ headers: true });
    const filename = `${countryName.replaceAll(' ', '-').toLowerCase()}.csv`;
    const writeStream = fs.createWriteStream(`./resources/stocks/${filename}`);
    csvStream.pipe(writeStream).on('end', () => writeStream.end());

    const context = await browser.newContext();
    let pageNum = 0;
    try {
        const page = await openStockScreenerPage(context);
        await selectCountry(page, countryName);
        let nextButtonVisible = await isNextButtonVisible(page);
        while (pageNum === 0 || nextButtonVisible) {
            pageNum += 1;
            const pageData = await collectPageData(page);
            savePageData(csvStream, pageData);
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
}
async function openStockScreenerPage(browserContext: playwright.BrowserContext): Promise<playwright.Page> {
    const page = await browserContext.newPage();
    try {
        await page.goto('https://www.investing.com/stock-screener/?sp=country::27|sector::a|industry::a|equityType::a|exchange::a%3Ename_trans;1', {waitUntil: 'domcontentloaded'});
        // await adjustTableColumns(page);
        await page.waitForTimeout(3000);
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

async function selectCountry(page: playwright.Page, countryName: string) {
    await page.click('[placeholder="Select country"]');
    await page.click(`#countriesUL > li:has-text("${countryName}")`);
    await page.waitForTimeout(randomInteger(4000, 6000));
}

async function isNextButtonVisible(page: playwright.Page) {
    return await page.isVisible('.text_align_lang_base_2 > a:has-text("Next")');
}

async function collectPageData(page: playwright.Page) {
    return await page.$$eval('#resultsTable > tbody tr', (equities) => {
        return equities.map(eq => {
            const name = eq.querySelector('td:nth-child(2)').textContent;
            const link = eq.querySelector('td:nth-child(2) a').getAttribute('href');
            const slug = link ? link.split('/').pop() : '';
            const symbol = eq.querySelector('td:nth-child(3)').textContent;
            const exchange = eq.querySelector('td:nth-child(4)').textContent;
            const sector = eq.querySelector('td:nth-child(5)').textContent;
            const industry = eq.querySelector('td:nth-child(6)').textContent;
            return {
                symbol,exchange,name,sector,industry,slug
            };
        });
    });
}


function savePageData(csv: CsvFormatterStream<any, any>, pageData: any[]) {
    pageData.forEach(data => csv.write(data));
}

async function selectNextPage(page: playwright.Page, pageToSelect: number) {
    return await page.click('.text_align_lang_base_2 > a:has-text("Next")');
}

collectStocks('Russia');
collectStocks('Hong Kong');
