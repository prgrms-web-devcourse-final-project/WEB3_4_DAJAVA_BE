'use client';

import React, { useEffect, useState } from 'react';
import Image from 'next/image';

import { constant } from '../../../libs/constant';
import { SubBannerProps } from './subBanner.types';
import styles from './subBanner.module.css';



const SubBanner: React.FC<SubBannerProps> = ({ data }) => {
  const [src, setSrc] = useState<{ url: string }[]>([]);

  useEffect(() => {
    const handleResize = () => {
      setSrc(data.map((i) => ({ url: i?.images[0].url })));
    };
    handleResize();

    window.addEventListener('resize', handleResize);

    return () => {
      window.removeEventListener('resize', handleResize);
    };
  }, [data]);

  return (
    src?.length > 0 && (
      <div className={styles.wrap}>
        <figure className={styles.container}>
          <Image
            src={src[0]?.url || `${constant.IMG_PATH}/no-data.png`}
            fill
            alt="sub-banner"
            placeholder="blur"
            blurDataURL={`${constant.IMG_PATH}/blur.png`}
          />
        </figure>
      </div>
    )
  );
};

export default SubBanner;
