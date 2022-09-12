declare module 'moex-api' {
    export default class MoexAPI {
        constructor(...args: any[]);

        boards(...args: any[]): void;

        engines(...args: any[]): void;

        filterByCurrency(...args: any[]): void;

        getSecurityInfo(...args: any[]): void;

        index(...args: any[]): void;

        markets(...args: any[]): void;

        securitiesDataRaw(...args: any[]): void;

        securitiesDefinitions(...args: any[]): void;

        securitiesMarketData(...args: any[]): void;

        securityDataRawExplicit(...args: any[]): void;

        securityDefinition(...args: any[]): void;

        securityMarketData(...args: any[]): Promise<any>;

        securityMarketDataExplicit(...args: any[]): void;

    }

}


