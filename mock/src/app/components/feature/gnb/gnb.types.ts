export interface MenuItem {
  id: string;
  children?: MenuItem[];
}

export interface WebGnbProps {
  locale: string;
}