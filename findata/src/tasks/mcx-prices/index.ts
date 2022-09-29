#!/usr/bin/env node

import MoexAPI from 'moex-api';
import connection from '../../common/connection';
import { getStocksByCountry } from '../../common/instrument.service';

const moexApi = new MoexAPI();

async function main() {
    const securities = await getStocksByCountry('Russia');

    const whenSecuritiesInserted = securities.map(s => {
        return moexApi.securityMarketData(s.symbol)
            .then(data => {
                const {SECID, LAST} = data;
                return {
                    symbol: `${SECID}.MCX`,
                    datetime: new Date(),
                    value: LAST,
                    currency: 'RUB'
                };
            })
            .then(price => connection('instrument_price').insert(price))
            .then(() => console.log(`${s.symbol} inserted`));
    });


    await Promise.allSettled(whenSecuritiesInserted)
        .catch(console.error)
        .finally(() => connection.destroy());
}

main();
