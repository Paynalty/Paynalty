const DEV_ENV = {
  API_BASE_URL: 'http://localhost:8080',
};

const PROD_ENV = {
  API_BASE_URL: 'https://api.your-domain.com',
};

export const ENV = __DEV__ ? DEV_ENV : PROD_ENV;
