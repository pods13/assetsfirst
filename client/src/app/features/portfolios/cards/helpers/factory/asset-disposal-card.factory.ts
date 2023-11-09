import { CardFactory } from '../card-factory';
import { CardContainerType } from '../../types/card-container-type';
import { AssetDisposalCard } from '../../types/in/asset-disposal-card';

export class AssetDisposalCardFactory extends CardFactory<AssetDisposalCard> {

  constructor(containerType: CardContainerType) {
    super(containerType);
  }

  create(): AssetDisposalCard {
    return {
      ...this.originCard,
      title: 'Asset Disposal',
      cols: 4,
      rows: 3
    };
  }
}
