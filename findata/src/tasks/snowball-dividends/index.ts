#!/usr/bin/env node

import connection from '../../common/connection';
import axios from 'axios';
import {addMonths} from '../../utils/add-months';
import {getClient} from '../../utils/client';

async function main() {
    const instruments = [
        {
            symbol: 'RU000A105328',
            exchange: 'MCX',
            assetInfoId: '0e978906-7a15-4083-9e62-bd31f95d6e5d'
        },
        {
            symbol: 'RU000A104KU3',
            exchange: 'MCX',
            assetInfoId: 'fcfec0b1-f134-40ee-a908-5bd6c458da88'
        },

    ]
    const client = await getClient();

    const whenDividendsSaved = instruments.map(instrument => {
        return getDividendHistoryByTicker(instrument.assetInfoId)
            .catch(e => {
                console.error(`Error during dividend data gathering for ${instrument.symbol + ':' + instrument.exchange}`);
                throw e;
            })
            .then(dividends => {
                return dividends.map((dividend: any) => {
                    const recordDate = convertDate(dividend.date);
                    const declareDate = convertDate(dividend.date);
                    const payDate = dividend.paymentDate ? convertDate(dividend.paymentDate) : addMonths(recordDate, 1);
                    return {
                        amount: dividend.amount ? dividend.amount : null,
                        declareDate,
                        recordDate,
                        payDate
                    };
                });
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

async function getDividendHistoryByTicker(assetInfoId: string) {
    const res = await axios.get(`https://snowball-income.com/extapi/api/public/asset-info/div-history?assetInfoId=${assetInfoId}`);
    return res.data;
}


function convertDate(dateStr: string) {
    //dateStr formatted as dd.MM.yyyy
    if (dateStr) {
        return new Date(dateStr);
        // return date.getDay() + '-' + (date.getMonth() + 1) + '-' + date.getFullYear();
    }
    return null;
}

main();
