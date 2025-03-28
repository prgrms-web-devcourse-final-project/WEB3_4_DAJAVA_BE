
import { constant } from "../../../libs/constant";
import { ItemData } from "./card.types";

const getImageSrc = (category: string): string => {
    const categoryImageMap: Record<string, string> = {
      PRODUCT: `${constant.IMG_PATH}/product.png`,
    };
    return categoryImageMap[category] || categoryImageMap.PRODUCT;
};

const getItemTitle = (matchingItem: ItemData | undefined, locale: string): string => {
      if (!matchingItem) return '-';
      const localeKey = locale === constant.DEFAULT_LOCALE ? '' : `_${locale}`;
      return matchingItem[`title${localeKey}`] || '-';
};

export {getImageSrc, getItemTitle};