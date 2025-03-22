import React from 'react';
import styles from './title.module.css';
import { TitleProps } from './title.types';


const Title: React.FC<TitleProps> = ({ title, description }) => {
  return (
    <div className={styles.wrap}>
      <h1 className={styles.title}>{title}</h1>
      <span className={styles.description}>{description}</span>
    </div>
  );
};

export default Title;
