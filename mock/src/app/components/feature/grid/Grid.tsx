'use client';

import React from 'react';


import styles from './grid.module.css';
import { GridProps } from './grid.types';
import Card from '../../common/card/Card';
import Title from '../../common/title/Title';



const Grid: React.FC<GridProps> = ({ data, fullWidth = false }) => {
  return (
    <React.Fragment>
    <Title title='다른 상품입니다..' description='다른 상품 확인하세요.' />
    <div className={styles.wrap}>
      {data?.map((item, index) => (
        <div key={index} className={styles.container}>
          <Card data={item} category="PRODUCT" fullWidth={fullWidth} />
        </div>
      ))}
    </div>
    </React.Fragment>
  );
};

export default Grid;
