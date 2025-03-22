const API_BASE_URL = process.env.NEXT_PUBLIC_API_URL || 'http://localhost:3000/api/v1';
const BASE_URL = process.env.NEXT_PUBLIC_DOMAIN_IP || 'http://localhost:3000';
const TOKEN = '';
const USER_SESSION = '';

const THEME_COLORS = {
    primary: 'rgb(80, 190, 241)',
    secondary: 'rgb(0, 47, 128)',
  };

export const config={
    API_BASE_URL,
    BASE_URL,
    THEME_COLORS,
    TOKEN,
    USER_SESSION
  }