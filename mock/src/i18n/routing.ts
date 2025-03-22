import {defineRouting} from 'next-intl/routing';
import { constant } from '../app/libs/constant';
 
export const routing = defineRouting({
  locales: constant.LOCALES,
  defaultLocale: constant.DEFAULT_LOCALE,
});