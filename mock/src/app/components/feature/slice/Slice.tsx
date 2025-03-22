import React from 'react';
import Title from '../../common/title/Title';
import Card from '../../common/card/Card';
import styles from './slice.module.css';
import { SliceProps } from './slice.types';


const Slice: React.FC<SliceProps> = ({ data }) => {
  const sliceData = data.slice(0, 10);

  return (
    <React.Fragment>
      <Title title='상품입니다.' description='상품 확인하세요.' />
      {data && (
        <div id="doctor" className={styles.container}>
          <div className={styles.element}>
            {sliceData?.map((element) => (
              <Card data={element} category="PRODUCT" key={element.id} fullWidth />
            ))}
          </div>
        </div>
      )}
    </React.Fragment>
  );
};

export default Slice;
