#!/usr/bin/env node

import MoexAPI from 'moex-api';
import connection from '../common/connection';

const moexApi = new MoexAPI();
//TODO get prices for all available securities instead of just preselected
const securities = ['GAZP', 'ROSN', 'AGRO', 'PLZL', 'PHOR', 'MGNT', 'MTSS', 'IRAO', 'MOEX'];

const whenSecuritiesInserted = securities.map(s => {
    return moexApi.securityMarketData(s)
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
        .then(() => console.log(`${s} inserted`));
});


Promise.allSettled(whenSecuritiesInserted)
    .finally(() => connection.destroy());
