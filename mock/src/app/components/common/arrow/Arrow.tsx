'use client';

import React from 'react';
import { ArrowProps } from './arrow.types';
import styles from './arrow.module.css';

/**
 * Arrow 컴포넌트
 * @param active - 작동 시 회전
 * @param color - 화살표 색
 * @param width - 화살표 크기
 * @param style - 화살표 스타일
 */
const Arrow: React.FC<ArrowProps> = ({ active = false, color = '#002f80', width = 7, style = 'solid' }) => {
  return (
    <span
      className={`${styles.arrow} ${active ? `${styles.down}` : ''}`}
      style={{ border: `${style} ${color}`, width: `${width}px`, height: `${width}px`, borderWidth: '0 2px 2px 0' }}
    />
  );
};

export default Arrow;
