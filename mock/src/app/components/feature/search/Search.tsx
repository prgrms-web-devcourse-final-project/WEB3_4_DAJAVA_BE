'use client';

import React, { useState, ChangeEvent, FormEvent } from 'react';

import Button from '../../common/button/Button';
import Icon from '../../common/icon/Icon';

import { SearchBarProps } from './search.types';
import styles from './search.module.css';



const Search: React.FC<SearchBarProps> = ({ params }) => {

  const [searchData, setSearchData] = useState<{ search: string }>({
    search: params ? decodeURIComponent(params) : '',
  });
  const [isLoading, setIsLoading] = useState<boolean>(false);

  const onChangeInput = (name: string, e: ChangeEvent<HTMLInputElement>) => {
    setSearchData((prev) => ({ ...prev, [name]: e.target.value }));
  };

  const onSearch = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    // To do...
    setIsLoading(false);
  };

  return (
    <div className={styles.wrap}>
      <form className={styles.container} onSubmit={onSearch}>
        <input
          name="search"
          className={styles.input}
          placeholder="검색할 내용을 입력해 주세요."
          value={searchData.search}
          onChange={(e) => onChangeInput('search', e)}
          disabled={isLoading}
        />
        <Button
          className="text"
          type="submit"
          disabled={isLoading}
          text={<Icon icon="search" width={22} height={22} color="#002f80" />}
        />
      </form>
    </div>
  );
};

export default Search;
