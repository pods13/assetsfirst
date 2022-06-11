import { Module } from '@nestjs/common';
import { StocksModule } from './stocks/stocks.module';

@Module({
  imports: [StocksModule],
  controllers: [],
  providers: [],
})
export class AppModule {}
