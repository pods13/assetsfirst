#!/usr/bin/env node

import { collectStocks } from './collect-stocks';
import {collectEtfs} from "./collect-etfs";

const args = process.argv.slice(2);

const country = args[0];
const exchanges = args[1] ? args[1].split(',') : undefined;

collectStocks(country, exchanges);
collectEtfs(country, exchanges);
