import { CardData } from '../card-data';

export interface DividendGoalsCardData extends CardData {
  items: PositionItem[];
  extraExpenses: number;
}

interface PositionItem {
  name: string;
  quantity: string;
}
