

interface Image {
    url: string;
  }

interface ElementData {
    id: string;
    category: string;
    images: Image[];
    title: string;
    type: string;
  }
  
 export interface SliceProps {
    data: ElementData[];
    
  }