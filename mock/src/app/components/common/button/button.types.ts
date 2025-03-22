import { MouseEvent, CSSProperties, ReactNode } from 'react';

  export interface ButtonProps {
    className?: string;
    name?: string;
    role?: string;
    type?: 'button' | 'submit' | 'reset';
    onClick?: (e: MouseEvent<HTMLButtonElement>) => void;
    text?: ReactNode;  // ReactNode로 수정
    disabled?: boolean;
    fullWidth?: boolean;
    style?: CSSProperties;
  }