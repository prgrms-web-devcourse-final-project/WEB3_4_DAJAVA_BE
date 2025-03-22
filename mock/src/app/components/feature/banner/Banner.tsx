'use client';

import React, { useEffect, useState } from 'react';
import Image from 'next/image';
import Flicking, { ViewportSlot } from '@egjs/react-flicking';
import { bannerPlugins } from './bannerPlugins';
import { HomeBannerProps } from './banner.types';

import styles from './banner.module.css';
import '@egjs/react-flicking/dist/flicking.css';
import '@egjs/react-flicking/dist/flicking-inline.css';
import { constant } from '../../../libs/constant';



const Banner: React.FC<HomeBannerProps> = ({ data }) => {
    const [src, setSrc] = useState<{ url: string[]; id: string }[]>([]);
  
    useEffect(() => {
        const handleResize = () => {
            setSrc(data.map((i: { images: any[]; id: any; }) => ({
              url: Array.isArray(i.images)
                ? i.images.map((image) => (typeof image === 'string' ? image : image.url))
                : [],
              id: i.id
            })));
        };
        handleResize();
        window.addEventListener('resize', handleResize);
        return () => {
          window.removeEventListener('resize', handleResize);
        };
      }, [data]);
  
    return (
      <div>
        {src && src.length > 0 && (
          <Flicking circular align="prev" plugins={bannerPlugins}>
            {src.map((panel) => (
              <span className={styles.wrap} key={panel.id}>
                <Image
                className={styles.image}
                src={panel.url[0]}
                alt="banner-images"
                fill 
                sizes="100vw"
                placeholder='blur'
                blurDataURL={`${constant.IMG_PATH}/blur.png`}
                priority  />
              </span>
            ))}
            <ViewportSlot>
              <div className="flicking-pagination" style={{ display: 'none' }} />
            </ViewportSlot>
          </Flicking>
        )}
      </div>
    );
};

export default Banner;
  