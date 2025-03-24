'use client';

import React from 'react';
import Link from 'next/link';

import { LogoProps } from './logo.types';
import styles from './logo.module.css';

const Logo: React.FC<LogoProps> = ({ locale }) => {

  return (
    <Link className={styles.wrap} href={`/${locale}`} role="figure">
      {/* <Image src={`${constant.IMG_PATH}/logo.png`} alt="logo" fill quality={100} /> */}
      <h1 className="blind">프로젝트 로고</h1>
    </Link>
  );
};

export default Logo;
