import enMessages from './messages/en.json';
import koMessages from './messages/ko.json';
import {constant} from './src/app/libs/constant'
 

export interface NextIntlConfig {
    defaultLocale: string;
    locales: string[];
    messages: Record<string, unknown>;
  }
  
const config: NextIntlConfig = {
  defaultLocale: constant.DEFAULT_LOCALE,
  locales:  constant.LOCALES,
  messages: {
    en: enMessages,
    ko: koMessages,
  },
};

export default config;
