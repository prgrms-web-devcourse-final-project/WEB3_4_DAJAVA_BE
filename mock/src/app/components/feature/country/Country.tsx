'use client';

import React from 'react';

import Icon from '../../common/icon/Icon';
import Arrow from '../../common/arrow/Arrow';
import { CountryProps } from './country.types';
import styles from './country.module.css';



const Country: React.FC<CountryProps> = ({ locale }) => {
  return (
    <ul className={styles.wrap}>
      <li>
        <Icon icon={locale} width={33} height={33} />
      </li>
      <li className={styles.arrow}>
        <Arrow active={false} />
      </li>
    </ul>
  );
};

export default Country;
