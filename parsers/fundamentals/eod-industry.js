import {getClient} from "../client.js";
import axios from "axios";
import cheerio from "cheerio";

const US_EXCHANGES = ['NYSE', 'NYSEARCA', 'NASDAQ'];

async function main() {
    const client = await getClient();
    const stocksRes = await client.get(`/stocks?limit=20`);
    const stocks = stocksRes.data.content;
    const stockByCompanyId = stocks.reduce((res, stock) => {
        const exchange = US_EXCHANGES.includes(stock.identifier.exchange) ? 'US' : stock.identifier.exchange;
        res[stock.identifier.symbol + '.' + exchange] = stock.companyId;
        return res;
    }, {});
    console.log(stockByCompanyId);
    Object.keys(stockByCompanyId);
    const res = await axios.get(`https://eodhistoricaldata.com/financial-summary/GAZP.MCX`);
    const $ = cheerio.load(res.data);
    const generalInfo = $(`#fund_api > .json_box`).text();
    const sectorName = parseData(generalInfo, 'GicSector');
    const industryGroupName = parseData(generalInfo, 'GicGroup');
    const industryName = parseData(generalInfo, 'GicIndustry');
    const subIndustryName = parseData(generalInfo, 'GicSubIndustry');
    const taxonomy = {sectorName, industryGroupName, industryName, subIndustryName};
    console.log(taxonomy);

    const industriesRes = await client.get(`/industries`);
    const industryNameById = industriesRes.data.filter(industry => industry.parentId)
        .reduce((res, industry) => {
            res[industry.name] = industry.id;
            return res;
        }, {});
    console.log(industryNameById);

    //get unique taxonomies and create them
}

function parseData(info, key) {
    const matches = info.match(generateRegExp(key));
    if (matches) {
        return matches[1];
    }
    console.warn(`Cannot parse key: ${key} from ${info}`);
    return matches;
}

function generateRegExp(prefix) {
    return new RegExp(prefix + `:\\s+'(.+)'`);
}

main();
