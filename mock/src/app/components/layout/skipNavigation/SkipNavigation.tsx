'use client';

import React from 'react';
import { usePathname } from 'next/navigation';

import { ButtonGroupItem, SkipNavigationProps } from './skipNavigation.types';
import styles from './skipNavigation.module.css';



const BUTTON_GROUP: ButtonGroupItem[] = [{ id: 'banner' }, { id: 'product' }, { id: 'footer' }];

const SkipNavigation: React.FC<SkipNavigationProps> = ({ locale }) => {
  const path = usePathname();

  const onClick = (id: string) => {
    const element = document.getElementById(id);

    if (element) {
      element.tabIndex = -1;
      element.focus();
    }
  };

  return (
    path === `/${locale}` &&
    BUTTON_GROUP.map((item) => (
      <button className={styles.wrap} onClick={() => onClick(item.id)} type="button" key={item.id}>
        {item?.id}
      </button>
    ))
  );
};

export default SkipNavigation;
