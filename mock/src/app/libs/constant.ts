const IMG_PATH = '/images';
const HOME_PAGE_URL = '.mockpage.com';
const DEFAULT_LOCALE = "ko";
const LOCALES = ['en', 'ko'];
const FOOTER_DATA: never[] = [];
const CODE_DATA: never[] = [];

const META_DATA={
  project:'5팀 프로젝트',
  title:'더미 페이지',
  description: '마지막 프로젝트',
  publisher:'5팀',
  creator:'',
  authors: "",
  viewport: 'width=device-width, initial-scale=1, maximum-scale=1.0, user-scalable=0, viewport-fit-cover',
  referrer: 'origin-when-cross-origin',
  keywords: ['Mock'],
  type:'website'
}

const GNB_MENU = [
  {
    id: '소개',
    href: '/info',
  },
  {
    id: '상품',
    href: 'product/sample',
    children: [
      { id: '샘플1', href: 'product/sample' },
      { id: '샘플2', href: 'product/other' },
    ],
  },
  {
    id: '알림',
    href: 'support/notice',
    children:[
      {
        id: 'FAQ',
        href: 'support/faq',
      },
      {  id: '공지',
        href: 'support/notice',}
    ]
  },

  {
    id: 'contact-us',
    href: '/contact-us',
  },
];



export const constant = {
    IMG_PATH ,
    HOME_PAGE_URL,
    DEFAULT_LOCALE,
    LOCALES,
    FOOTER_DATA,
    CODE_DATA,
    META_DATA,
    GNB_MENU
  };
  