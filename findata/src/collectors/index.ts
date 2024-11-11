#!/usr/bin/env node

import {collectInstruments} from "./collect-instruments";
import extraEtfs from './data/etf/add.json';
import stocksToModify from "./data/stock/modify.json";

const args = process.argv.slice(2);

const country = args[0];
const exchanges = args[1] ? args[1].split(',') : [];

collectInstruments('stock', country, exchanges, [], stocksToModify);
collectInstruments('etf', country, exchanges, extraEtfs, {});
