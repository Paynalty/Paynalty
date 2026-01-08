import { AppsInToss } from '@apps-in-toss/framework';
import { PropsWithChildren, useEffect } from 'react';
import { AppState, AppStateStatus, Platform } from 'react-native';
import { InitialProps } from '@granite-js/react-native';
import { context } from '../require.context';
import { TDSProvider } from '@toss/tds-react-native';
import { QueryClientProvider, focusManager } from '@tanstack/react-query';
import { queryClient } from './queryClient';
import { GestureHandlerRootView } from 'react-native-gesture-handler';

function onAppStateChange(status: AppStateStatus) {
  if (Platform.OS !== 'web') {
    focusManager.setFocused(status === 'active');
  }
}

function AppContainer({ children }: PropsWithChildren<InitialProps>) {
  useEffect(() => {
    const subscription = AppState.addEventListener('change', onAppStateChange);
    return () => subscription.remove();
  }, []);

  return (
    <GestureHandlerRootView style={{ flex: 1 }}>
      <QueryClientProvider client={queryClient}>
        <TDSProvider>
          <>{children}</>
        </TDSProvider>
      </QueryClientProvider>
    </GestureHandlerRootView>
  );
}

export default AppsInToss.registerApp(AppContainer, { context });
