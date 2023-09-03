import { AllocationCardFactory } from './factory/allocation-card.factory';
import { CardContainerType } from '../types/card-container-type';
import { DashboardCard } from '../types/dashboard-card';
import { DividendIncomeCardFactory } from './factory/dividend-income-card.factory';
import { DividendGoalsCardFactory } from './factory/dividend-goals-card.factory';
import { SectoralDistributionCardFactory } from './factory/sectoral-distribution-card.factory';
import { BalanceCardFactory } from './factory/balance-card.factory';
import { ContributionCardFactory } from './factory/contribution-card.factory';
import { InvestmentYieldCardFactory } from './factory/investment-yield-card.factory';
import { EquityRiskPremiumCardFactory } from './factory/equity-risk-premium-card.factory';


export function createCard(containerType: CardContainerType): DashboardCard {
  const cardFactory = cardContainerTypeByFactory[containerType];
  if (!cardFactory) {
    throw new Error(`Cannot find card factory for ${containerType}`);
  }
  return new cardFactory(containerType).create();
}

const cardContainerTypeByFactory = {
  [CardContainerType.ALLOCATION]: AllocationCardFactory,
  [CardContainerType.DIVIDEND_INCOME]: DividendIncomeCardFactory,
  [CardContainerType.DIVIDEND_GOALS]: DividendGoalsCardFactory,
  [CardContainerType.SECTORAL_DISTRIBUTION]: SectoralDistributionCardFactory,
  [CardContainerType.BALANCE]: BalanceCardFactory,
  [CardContainerType.CONTRIBUTION]: ContributionCardFactory,
  [CardContainerType.INVESTMENT_YIELD]: InvestmentYieldCardFactory,
  [CardContainerType.EQUITY_RISK_PREMIUM]: EquityRiskPremiumCardFactory,
};
