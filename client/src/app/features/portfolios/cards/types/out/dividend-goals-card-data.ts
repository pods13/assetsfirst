import { CardData } from '../card-data';

export interface DividendGoalsCardData extends CardData {
  items: PositionItem[];
}

interface PositionItem {
  name: string;
  quantity: string;
}
