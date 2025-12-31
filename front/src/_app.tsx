import { AppsInToss } from '@apps-in-toss/framework';
import { PropsWithChildren } from 'react';
import { InitialProps } from '@granite-js/react-native';
import { context } from '../require.context';
import { TDSProvider } from '@toss/tds-react-native';
import { QueryClientProvider } from '@tanstack/react-query';
import { queryClient } from './queryClient';

function AppContainer({ children }: PropsWithChildren<InitialProps>) {
  return (
    <QueryClientProvider client={queryClient}>
      <TDSProvider>
        <>{children}</>
      </TDSProvider>
    </QueryClientProvider>
  );
}

export default AppsInToss.registerApp(AppContainer, { context });
