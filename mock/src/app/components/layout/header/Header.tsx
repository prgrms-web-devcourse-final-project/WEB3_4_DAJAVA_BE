import React from 'react';
import { cookies } from 'next/headers';

import Logo from '../../common/logo/Logo';
import Gnb from '../../feature/gnb/Gnb';
import Country from '../../feature/country/Country';
import AuthGnb from '../../feature/authGnb/AuthGnb';
import { config } from '../../../libs/config';

import { HeaderProps } from './header.types';
import styles from './header.module.css';


const Header = async ({ locale }: HeaderProps) => {
  console.log(locale)
  const store = cookies();
  const session = (await store).get(config.USER_SESSION);

  return (
    <header className={styles.wrap}>
      <div className={styles.container}>
        <div className={styles.logo}>
          <Logo locale={locale} />
        </div>
        <div className={styles.auth}>
          <AuthGnb session={session} locale={locale} />
          <Country locale={locale} />
        </div>
        <Gnb locale={locale} />
      </div>
    </header>
  );
};

export default Header;
