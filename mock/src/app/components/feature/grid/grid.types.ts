import { CardProps } from "../../common/card/card.types";

export interface GridProps {
  data: CardProps['data'][];
  category?: string;
  fullWidth?: boolean;
}