import React from 'react';
import { AppRegistry } from 'react-native';
import App from './src/_app';

// register(App);
AppRegistry.registerComponent('shared', () => (props) => React.createElement(App as any, props));
