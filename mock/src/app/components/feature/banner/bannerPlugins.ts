import { AutoPlay, Pagination } from "@egjs/flicking-plugins";

export const bannerPlugins = [
    new AutoPlay({ duration: 8000, direction: 'NEXT', stopOnHover: false }),
    new Pagination({ type: 'fraction' }),
  ];