
import { CARD_DATA } from "../components/common/card/cardData";
import Banner from "../components/feature/banner/Banner";
import BANNERDATA from "../components/feature/banner/bannerData";
import Search from "../components/feature/search/Search";
import SubBanner from "../components/feature/subBanner/SubBanner";
import SUB_BANNER_DATA from "../components/feature/subBanner/subBannerData";
import Footer from "../components/layout/footer/Footer";
import Header from "../components/layout/header/Header";
import styles from "./page.module.css";
import Slice from "../components/feature/slice/Slice";
import Grid from "../components/feature/grid/Grid";

export default function Home() {
  return (
    <div className={styles.page}>
      <main className={styles.main}>
        <Header locale="ko"/>
        <Banner data={BANNERDATA}/>
        <Search/>
        <Slice data={CARD_DATA}/>
        <Grid data={CARD_DATA} />
        <SubBanner data={SUB_BANNER_DATA}/>
        <Footer/>
        </main>
    </div>
  );
}
