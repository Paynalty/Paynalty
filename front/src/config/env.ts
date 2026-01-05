const DEV_ENV = {
  // Android Emulator: 10.0.2.2
  // iOS Simulator: localhost
  // Real Device: Mac IP (e.g., 192.168.x.x)
  API_BASE_URL: 'http://localhost:8080',
  // API_BASE_URL: 'http://15.164.110.188:8080',
};

const PROD_ENV = {
  API_BASE_URL: 'http://15.164.110.188:8080',
};

export const ENV = __DEV__ ? DEV_ENV : PROD_ENV;
