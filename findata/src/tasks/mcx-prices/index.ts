#!/usr/bin/env node

import MoexAPI from 'moex-api';
import connection from '../../common/connection';
import { getInstruments, getStocksByCountry } from '../../common/instrument.service';

const moexApi = new MoexAPI();

async function main() {
    const instruments = await getInstruments(connection, ['MCX']);

    const whenSecuritiesInserted = instruments.map(instrument => {
        return moexApi.securityMarketData(instrument.symbol)
            .then(data => {
                const {SECID, LAST} = data;
                if (!LAST) {
                    console.warn(`Got empty price for ${instrument.symbol}`);
                    return null;
                }
                return {
                    instrument_id: instrument.id,
                    datetime: new Date(),
                    value: LAST
                };
            })
            .then(res => {
                if (res) {
                    return connection('instrument_price').insert(res)
                        .catch(e => console.error(e))
                        .then(() => console.log(`${instrument.symbol} inserted`));
                } else {
                    return Promise.resolve();
                }
            });
    });


    await Promise.allSettled(whenSecuritiesInserted)
        .catch(console.error)
        .finally(() => connection.destroy());
}

main();
