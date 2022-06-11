import { Controller, Get, Param, Query } from '@nestjs/common';
import { StockCompanyProfile } from './stock-company-profile';
import { EodHdService } from './eod-hd.service';

@Controller('stocks')
export class StocksController {

    constructor(private eodHdService: EodHdService) {}

    @Get(`/:id/company`)
    async getStockCompanyProfile(@Param('id') id: string): Promise<StockCompanyProfile> {
        return await this.eodHdService.getStockCompanyProfile(id);
    }
}
