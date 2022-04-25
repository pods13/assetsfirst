import {getClient} from "../client.js";
import axios from "axios";
import cheerio from "cheerio";

const US_EXCHANGES = ['NYSE', 'NYSEARCA', 'NASDAQ'];

async function main() {
    const client = await getClient();

    const industriesRes = await client.get(`/industries`);
    let cachedIndustryNameById = industriesRes.data.filter(industry => industry.parentId)
        .reduce((res, industry) => {
            res[industry.name] = industry.id;
            return res;
        }, {});

    //TODO support iteration over pages of stocks
    const stocksRes = await client.get(`/stocks?limit=20`);
    const stocks = stocksRes.data.content;
    const tickerByCompanyId = stocks.reduce((res, stock) => {
        const exchange = US_EXCHANGES.includes(stock.identifier.exchange) ? 'US' : stock.identifier.exchange;
        res[stock.identifier.symbol + '.' + exchange] = stock.companyId;
        return res;
    }, {});

    const taxonomies = await parseTaxonomies(Object.keys(tickerByCompanyId));
    const knowsIndustries = Object.keys(cachedIndustryNameById);
    const whenMissingIndustriesCreated = taxonomies
        .filter(taxonomy => !knowsIndustries.includes(taxonomy.subIndustryName))
        .map(taxonomy => client.post('/industries', taxonomy));
    const resolvedIndustries = await Promise.allSettled(whenMissingIndustriesCreated);
    const createdIndustries = resolvedIndustries.filter(res => {
        if (res.reason) {
            console.warn(res.reason?.response?.data?.message);
            return false;
        }
        return true;
    }).map(res => res.value.data);
    const industryNameById = createdIndustries.reduce((res, industry) => {
        res[industry.name] = industry.id;
        return res;
    }, {});
    cachedIndustryNameById = {...cachedIndustryNameById, ...industryNameById};

    await patchCompanyIndustry(client, taxonomies, tickerByCompanyId, cachedIndustryNameById);
}

async function patchCompanyIndustry(client, taxonomies, tickerByCompanyId, cachedIndustryNameById) {
    const whenCompanyIndustryPatched = taxonomies
        .filter(taxonomy => cachedIndustryNameById[taxonomy.subIndustryName])
        .map(taxonomy => {
            const companyId = tickerByCompanyId[taxonomy.ticker];
            const subIndustryId = cachedIndustryNameById[taxonomy.subIndustryName];
            return client.patch(`/companies/${companyId}`, {subIndustryId});
        });
    const resolvedCompanies = await Promise.allSettled(whenCompanyIndustryPatched);
    const patchedCompanyNames = resolvedCompanies.filter(res => {
        if (res.reason) {
            console.warn(res.reason?.response?.data?.message);
            return false;
        }
        return true;
    })
        .map(res => res.value?.data?.name);
    console.log(patchedCompanyNames);
}

async function parseTaxonomies(tickers) {
    const whenTaxonomyParsed = tickers
        .map(ticker => parseTaxonomy(ticker));
    const resolvedTaxonomies = await Promise.allSettled(whenTaxonomyParsed);
    return resolvedTaxonomies.filter(res => {
        if (res.reason) {
            console.warn(res.reason);
            return false;
        }
        return true;
    }).map(res => res.value);
}

async function parseTaxonomy(ticker) {
    const res = await axios.get(`https://eodhistoricaldata.com/financial-summary/${ticker}`);
    const $ = cheerio.load(res.data);
    const generalInfo = $(`#fund_api > .json_box`).text();
    const sectorName = parseData(generalInfo, 'GicSector');
    const industryGroupName = parseData(generalInfo, 'GicGroup');
    const industryName = parseData(generalInfo, 'GicIndustry');
    const subIndustryName = parseData(generalInfo, 'GicSubIndustry');
    return {ticker, sectorName, industryGroupName, industryName, subIndustryName};
}

function parseData(info, key) {
    const matches = info.match(generateRegExp(key));
    if (matches) {
        return matches[1];
    }
    const partInfo = info.replace(/\s/g, '');
    throw new Error(`Cannot parse key: ${key} from ${partInfo}`);
}

function generateRegExp(prefix) {
    return new RegExp(prefix + `:\\s+'(.+)'`);
}

main();
