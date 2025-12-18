import {appsInToss} from '@apps-in-toss/framework/plugins';
import {defineConfig} from '@granite-js/react-native/config';

export default defineConfig({
    scheme: 'intoss',
    appName: 'paynalty',
    plugins: [
        appsInToss({
            brand: {
                displayName: '페이널티', // 화면에 노출될 앱의 한글 이름
                primaryColor: '#3182F6', // 화면에 노출될 앱의 기본 색상
                icon: 'https://static.toss.im/appsintoss/11149/88b088d4-24ea-4d39-9503-55e53608f7f1.png',
                bridgeColorMode: 'basic',
            },
            permissions: [],
        }),
    ],
});