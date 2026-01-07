import { appsInToss } from '@apps-in-toss/framework/plugins';
import { defineConfig } from '@granite-js/react-native/config';

export default defineConfig({
  scheme: 'intoss',
  appName: 'paynalty',
  plugins: [
    appsInToss({
      brand: {
        displayName: '페이널티', // 화면에 노출될 앱의 한글 이름
        primaryColor: '#3182F6', // 화면에 노출될 앱의 기본 색상
        icon: 'https://static.toss.im/appsintoss/11149/212e2799-d5bd-4f72-a2fe-6bf2c72da56f.png',
      },
      permissions: [
        { name: 'camera', access: 'access' },
        { name: 'photos', access: 'read' },
      ],
    }),
  ],
});
