interface MetadataParams {
    params: {
      locale: string;
    };
  }
  
interface Metadata {
    locale: string;
    site_name: string;
    title: string;
    description: string;
    type: string;
    url: string;
    canonical: string;
    icons: {
      icon: string;
      apple: string;
      shortcut: string;
    };
    openGraph: {
      locale: string;
      title: string;
      description: string;
      type: string;
      url: string;
      canonical: string;
      images: {
        url: string;
        width: number;
        height: number;
        alt: string;
      }[];
    };
    publisher: string;
    creator: string;
    authors: string;
    themeColor: string;
    colorScheme: string;
    viewport: string;
    referrer: string;
    formatDetection: {
      email: boolean;
      address: boolean;
      telephone: boolean;
    };
    keywords: string[];
  }
  
  export {MetadataParams, Metadata};