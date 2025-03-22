'use client';

import React from 'react';
import styles from './icon.module.css'
import { Icons } from './Icons';
import { IconProps } from './icon.types';

/**
 * @param icon : 아이콘 종류
 * @param color : 아이콘 색
 * @param width : 너비
 * @param height : 높이
 */
const Icon: React.FC<IconProps> = ({ icon, color = '#000', width = 24, height = 24 }) => {
  const iconStyle = {
    display: 'inline-block',
    '--icon': Icons(icon, color),
    width: typeof width === 'number' ? `${width}px` : width,
    height: typeof height === 'number' ? `${height}px` : height,
  };

  return (
    <span style={iconStyle}>
      <i className={styles.svg} />
    </span>
  );
};

export default Icon;
