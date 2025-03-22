'use client';

import React, { useState } from 'react';
import { constant } from '../../../libs/constant';
import { MenuItem, WebGnbProps } from './gnb.types';
import styles from './gnb.module.css';

const Gnb: React.FC<WebGnbProps> = () => {
  const [currentCategory, setCurrentCategory] = useState<string | null>(null);

  const handleMouseEvent = (id: string | null) => {
    if (id) setCurrentCategory(id);
    else setCurrentCategory(null);
  };

  const renderMenu = (menu: MenuItem) => {
    return (
      <li
        className={`${styles.list} ${currentCategory === menu.id ? styles.active : ''}`}
        key={menu.id}
        onMouseOver={() => handleMouseEvent(menu.id)}
        onMouseLeave={() => handleMouseEvent(null)}
        onFocus={() => handleMouseEvent(menu.id)}
      >
        <span>{menu.id}</span>
        {menu.children && currentCategory === menu.id && (
          <div className={styles.childrenWrap}>
            {menu.children.map((child: MenuItem) => (
              <span key={child.id} >
                {child.id}
              </span>
            ))}
          </div>
        )}
      </li>
    );
  };

  return (
    <div className={styles.wrap}>
      <ul className={styles.container}>
        {constant.GNB_MENU.map((menu: MenuItem) => renderMenu(menu))}
      </ul>
    </div>
  );
};

export default Gnb;
