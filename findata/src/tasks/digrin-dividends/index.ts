#!/usr/bin/env node
import {getInstruments} from "../../common/instrument.service";
import connection from "../../common/connection";
import {getClient} from "../../utils/client";
import axios from "axios";
import {load} from "cheerio";
import {convertToYahooTicker} from '../../utils/ticker';
import UserAgent from 'user-agents';

const args = process.argv.slice(2);

const exchanges = args[0] ? args[0].split(',') : [];
const inAnyPortfolio = true;

main(exchanges, inAnyPortfolio);

async function main(exchanges: string[], inAnyPortfolio: boolean) {
    const instruments = await getInstruments(connection, exchanges, inAnyPortfolio);
    const client = await getClient();

    const whenDividendsSaved = instruments.map(instrument => {
        return getDividendHistoryByTicker(convertToYahooTicker(instrument))
            .catch(e => {
                console.error(`Error during dividend data gathering for ${instrument.symbol + ':' + instrument.exchange} : ${e.message}`, e);
                throw e;
            })
            .then(res => {
                if (res) {
                    return client.post(`/dividends?symbol=${instrument.symbol}&exchange=${instrument.exchange}`, res)
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

async function getDividendHistoryByTicker(ticker: string) {
    const res = await axios.get(`https://www.digrin.com/stocks/detail/${ticker}`, {
        headers: {
            'User-Agent': new UserAgent().toString(),
            'Accept': 'text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8',
            'Accept-Language': 'en-US,en;q=0.5',
            'Accept-Encoding': 'gzip, deflate, br',
            'Connection': 'keep-alive',
            'Upgrade-Insecure-Requests': '1'
        }
    });
    const $ = load(res.data);
    const dividendsTable = $(`table.table.table-striped > tbody`);
    const result: any[] = [];
    dividendsTable.find('tr').each((i: any, divDataEl: any) => {
        const divData: any = {};
        load(divDataEl)('td').each((index: any, el: any) => {
            if (index === 0) {
                divData['declareDate'] = $(el).text();
                divData['recordDate'] = $(el).text();
            } else if (index === 1) {
                divData['payDate'] = $(el).text();
            } else if (index === 2) {
                const dividendAmountAsText = $(el).contents().filter(function() {
                    return this.nodeType === 3;
                }).text();
                divData['amount'] =  parseAmount(dividendAmountAsText)
            }
        });
        result.push(divData);
    });
    return result;
}

const parseAmount = (amount: string): number => {
    const matched = amount.match(/[\d\.]+/);
    return matched ? +matched[0]: 0;
}