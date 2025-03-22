import { constant } from '../../libs/constant';
import { config } from '../../libs/config';
import type { Metadata } from 'next'
import { MetadataParams } from '../../types/metaData';


export const generateMetadata = async ({ params: { locale } }: MetadataParams): Promise<Metadata> => {
  return {
    title: constant.META_DATA.title,
    description: constant.META_DATA.description,
    icons: {
      icon: `${constant.IMG_PATH}/favicon.svg`,
      apple: `${constant.IMG_PATH}/favicon.svg`,
      shortcut: `${constant.IMG_PATH}/favicon.svg`,
    },
    publisher: constant.META_DATA.publisher,
    creator: constant.META_DATA.creator,
    themeColor: config.THEME_COLORS.primary,
    viewport: constant.META_DATA.viewport,
    formatDetection: {
      email: false,
      address: false,
      telephone: false,
    },
    keywords: constant.META_DATA.keywords
  };
};
