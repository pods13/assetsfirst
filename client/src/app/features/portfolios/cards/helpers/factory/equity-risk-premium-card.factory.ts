import { CardFactory } from '../card-factory';
import { CardContainerType } from '../../types/card-container-type';
import { EquityRiskPremiumCard } from '../../types/in/equity-risk-premium-card';

export class EquityRiskPremiumCardFactory extends CardFactory<EquityRiskPremiumCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): EquityRiskPremiumCard {
    return {
      ...this.originCard,
      title: 'Risk Premium',
      cols: 4,
      rows: 3
    };
  }
}
