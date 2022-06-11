import { Module } from '@nestjs/common';
import { StocksController } from './stocks.controller';
import { EodHdService } from './eod-hd.service';

@Module({
    controllers: [StocksController],
    providers: [EodHdService],
})
export class StocksModule {}
