import { CardFactory } from '../card-factory';
import { CardContainerType } from '../../types/card-container-type';
import { BalanceCard } from '../../types/in/balance-card';

export class BalanceCardFactory extends CardFactory<BalanceCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): BalanceCard {
    return {
      ...this.originCard,
      title: 'Balance',
      cols: 2,
      rows: 1,
      minItemCols: 2,
      minItemRows: 1,
    };
  }
}
