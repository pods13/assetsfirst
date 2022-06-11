import { BadRequestException, Injectable } from '@nestjs/common';
import { StockCompanyProfile } from './stock-company-profile';
import axios from 'axios';
import * as cheerio from 'cheerio';
import * as randUserAgent from 'rand-user-agent';

@Injectable()
export class EodHdService {

    async getStockCompanyProfile(id: string): Promise<StockCompanyProfile> {
        const res = await this.getFinancialSummary(id);
        if (res.status !== 200 || !res.data) {
            throw new BadRequestException(`Cannot get data for stock with id: ${id}`);
        }
        const $ = cheerio.load(res.data);
        const generalInfo = $(`#fund_api > .json_box`).text();
        const companyName = this.parseData(generalInfo, 'Name');
        const isin = this.parseData(generalInfo, 'ISIN');
        const sector = this.parseData(generalInfo, 'Sector');
        const industry = this.parseData(generalInfo, 'Industry');
        return {
            companyName,
            isin,
            sector,
            industry
        };
    }

    async getFinancialSummary(id: string) {
        const agent = randUserAgent("desktop");
        const acceptHeader = `text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9`;
        try {
            return await axios.get(`https://eodhistoricaldata.com/financial-summary/${id}`,
                { timeout: 3000 , headers: {'User-agent': agent, 'Accept': acceptHeader}});
        } catch (e) {
            throw e;
        }
    }

    private parseData(info, key) {
        const matches = info.match(this.generateRegExp(key));
        if (matches) {
            return matches[1];
        }
        const partInfo = info.replace(/\s/g, '');
        throw new Error(`Cannot parse key: ${key} from ${partInfo}`);
    }


    private generateRegExp(prefix) {
        return new RegExp(prefix + `:\\s+'(.+)'`);
    }
}
