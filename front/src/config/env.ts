const DEV_ENV = {
    // Android Emulator: 10.0.2.2
    // iOS Simulator: localhost
    // Real Device: Mac IP (e.g., 192.168.x.x)
    API_BASE_URL: 'http://localhost:8080',
    // API_BASE_URL: 'https://paynalty.store',
};

const PROD_ENV = {
    API_BASE_URL: 'https://paynalty.store',
};

export const ENV = __DEV__ ? DEV_ENV : PROD_ENV;
