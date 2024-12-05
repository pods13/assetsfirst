#!/usr/bin/env node

import MoexAPI from 'moex-api';
import connection from '../../common/connection';
import { getInstruments } from '../../common/instrument.service';
import {Instrument} from "../../common/types/instrument";

const moexApi = new MoexAPI();

const getQuote = async (instrument: Instrument) => {
    return moexApi.securityMarketData(instrument.symbol)
        .then(data => {
            const {SECID, LAST, securityInfo} = data;
            const prevPrice = securityInfo?.['PREVPRICE'];
            if (!LAST && !prevPrice) {
                console.warn(`Got empty price for ${instrument.symbol}`);
                return null;
            }
            return {
                instrument_id: instrument.id,
                datetime: new Date(),
                value: LAST ?? prevPrice
            };
        })
}

async function main() {
    const instruments = await getInstruments(connection, ['MCX']);

    const whenSecuritiesInserted = instruments.map(instrument => {
        return getQuote(instrument)
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

// main();

getQuote({id:1 , symbol: 'AGRO', exchange: 'MCX'})
.then(console.log)