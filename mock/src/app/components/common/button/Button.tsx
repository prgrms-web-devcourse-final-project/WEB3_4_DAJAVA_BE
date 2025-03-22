'use client';

import React, { useRef, useState, MouseEvent, KeyboardEvent} from 'react';
import Icon from '../icon/Icon';
import { ButtonProps } from './button.types';
import styles from './button.module.css';



/**
 * @param className - 버튼 모양 변경 (out-line || text)
 * @param name - 버튼 이름
 * @param role - 버튼 역할 (접근성을 위한)
 * @param type - 버튼 역할 (submit || button || reset)
 * @param onClick - 버튼 클릭 이벤트
 * @param text - 버튼 텍스트
 * @param disabled - 버튼 비활성화 여부
 * @param fullWidth - 버튼 너비를 100%로 설정
 * @param style - 버튼 커스텀 스타일
 */

const Button: React.FC<ButtonProps> = ({
  className = '',
  name = 'default',
  role,
  type = 'button',
  onClick,
  text = '',
  disabled = false,
  fullWidth = false,
  style,
}) => {
  const [isLoading, setIsLoading] = useState(false);
  const buttonRef = useRef<HTMLButtonElement>(null);

  const onAction = (e: MouseEvent<HTMLButtonElement>) => {
    if (onClick) {
      setIsLoading(true);
      const ele = buttonRef.current;

      if (ele) {
        const { top, left } = ele.getBoundingClientRect();
        const x = e.clientX - left;
        const y = e.clientY - top;

        const action = document.createElement('span');
        action.className = 'active';
        action.style.left = `${x}px`;
        action.style.top = `${y}px`;

        ele.appendChild(action);

        setTimeout(() => {
          action.remove();
        }, 600);

        setIsLoading(false);
        onClick?.(e);
      } else {
        setIsLoading(false);
      }
    }
  };

  const onKeyDown = (e: KeyboardEvent<HTMLButtonElement>) => {
    setIsLoading(true);
    const KEY = e.key || e.keyCode;
    const ENTER = 'Enter';

    if (KEY === ENTER || KEY === 13) {
      setIsLoading(false);
      onAction(e as unknown as MouseEvent<HTMLButtonElement>);
    }

    setIsLoading(false);
  };

  return (
    <button
      className={`${styles.button} ${styles[className || '']} ${fullWidth ? 'fullWidth' : ''}`}
      name={name}
      role={role}
      type={type}
      onClick={onAction}
      onKeyDown={onKeyDown}
      disabled={disabled}
      aria-label={`${name}-button`}
      aria-disabled={disabled}
      style={style}
      ref={buttonRef}
    >
      {isLoading ? (
        <p className={styles.icon}>
          <Icon icon="loading" width={20} height={20} color="#002f80" />
        </p>
      ) : (
        text
      )}
    </button>
  );
};

export default Button;
