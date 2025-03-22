interface BannerData {
    id: string;
    images: { url: string }[] | string[];
  }

export interface HomeBannerProps {
    data: BannerData[];
  }