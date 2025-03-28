'use client';

import React, { useContext } from 'react';
import { useParams } from 'next/navigation';
import Image from 'next/image';

import { constant } from '../../../libs/constant';
import { getImageSrc, getItemTitle } from './cardUtil';

import styles from './card.module.css';
import { CardProps, ItemData } from './card.types';

const Card: React.FC<CardProps> = ({ category = 'PRODUCT', data, fullWidth = false }) => {
  const param = useParams();
  const { id = '', images = [], title = '-', type = '-' } = data;
  const locale = Array.isArray(param?.locale) ? param?.locale[0] : param?.locale || constant.DEFAULT_LOCALE;
  const imageSrc = images[0]?.url || getImageSrc(category);


  return (
    <article className={`${styles.container} ${fullWidth ? 'fullWidth' : ''}`} role="document">
      <section className={styles.wrap} role="figure">
        <figure className={styles.image}>
          <Image
            src={imageSrc}
            alt="card-image"
            sizes="25vw"
            placeholder="blur"
            blurDataURL={`${constant.IMG_PATH}/blur.png`}
            fill
          />
          <figcaption className="blind">{title}</figcaption>
        </figure>
        <ul className={styles.description}>
        <li className={styles[category.toLowerCase()]}>
            상품
          </li>
          <li className={styles.title}><h2>{title}</h2></li>
        </ul>
      </section>
    </article>
  );
};

export default Card;
