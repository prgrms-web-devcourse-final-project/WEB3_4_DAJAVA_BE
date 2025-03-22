export interface ImageData {
  url?: string;
}

export interface ItemData {
  id: string;
  title: string;
  [key: string]: any;
}

export interface CardData {
  id:string,
  images?: ImageData[];
  hospitalTitle?: string;
  title?: string;
  subject?: string;
  numOfStaff?: number | string;
  type?: string;
}

export interface CardProps {
  category?: 'PRODUCT';
  data: CardData;
  fullWidth?: boolean;
}
