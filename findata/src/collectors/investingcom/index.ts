import { collectStocks } from './collect-stocks';

const args = process.argv.slice(2);

const country = args[0];
const exchanges = args[1] ? args[1].split(',') : undefined;

collectStocks(country, exchanges);
