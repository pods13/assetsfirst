import {AllocationCardComponent} from '../containers/allocation-card/allocation-card.component';
import {CardContainerType} from './card-container-type';
import {DividendIncomeCardComponent} from '../containers/dividend-income-card/dividend-income-card.component';
import {DividendGoalsCardComponent} from '../containers/dividend-goals-card/dividend-goals-card.component';
import {SectoralDistributionCardComponent} from '../containers/sectoral-distribution-card/sectoral-distribution-card.component';
import {BalanceCardComponent} from '../containers/balance-card/balance-card.component';
import {ContributionCardComponent} from '../containers/contribution-card/contribution-card.component';
import {InvestmentYieldCardComponent} from '../containers/investment-yield-card/investment-yield-card.component';
import {EquityRiskPremiumComponent} from '../containers/equity-risk-premium/equity-risk-premium.component';
import {AssetDisposalCardComponent} from '../containers/asset-disposal-card/asset-disposal-card.component';

export const cardContainerTemplateMapper = {
    [CardContainerType.ALLOCATION]: AllocationCardComponent,
    [CardContainerType.DIVIDEND_INCOME]: DividendIncomeCardComponent,
    [CardContainerType.DIVIDEND_GOALS]: DividendGoalsCardComponent,
    [CardContainerType.SECTORAL_DISTRIBUTION]: SectoralDistributionCardComponent,
    [CardContainerType.BALANCE]: BalanceCardComponent,
    [CardContainerType.CONTRIBUTION]: ContributionCardComponent,
    [CardContainerType.INVESTMENT_YIELD]: InvestmentYieldCardComponent,
    [CardContainerType.EQUITY_RISK_PREMIUM]: EquityRiskPremiumComponent,
    [CardContainerType.ASSET_DISPOSAL]: AssetDisposalCardComponent,
};
