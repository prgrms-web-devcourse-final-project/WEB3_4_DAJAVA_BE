import React from 'react';
import styles from'./authGnb.module.css'
import { AuthGnbProps } from './authGnb.types';

const AuthGnb: React.FC<AuthGnbProps> = ({ session, locale }) => {
    return (
    <ul className={styles.list}>
      <li className={styles.link}>
      {session ? <span>로그아웃</span>: <span>로그인</span>}
      </li>
      <li className={styles.link}>
      {session ? <span>마이페이지</span>: <span>회원가입</span>}
      </li>
    </ul>
  );
};

export default AuthGnb;
