import { register } from '@granite-js/react-native';
import { AppRegistry } from 'react-native';
import App from './src/_app';

register(App);
AppRegistry.registerComponent('shared', () => App);
