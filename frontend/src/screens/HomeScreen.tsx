import React from 'react';
import { View, Text, StyleSheet } from 'react-native';

const HomeScreen = () => {
  return (
    <View style={styles.container}>
      <Text style={styles.title}>Paynalty</Text>
      <Text style={styles.subtitle}>벌금 관리 앱</Text>
      <Text style={styles.info}>Expo 웹 환경에서 실행 중입니다! 🎉</Text>
    </View>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: '#f5f5f5',
  },
  title: {
    fontSize: 32,
    fontWeight: 'bold',
    color: '#5C8DFF',
    marginBottom: 8,
  },
  subtitle: {
    fontSize: 18,
    color: '#666',
    marginBottom: 24,
  },
  info: {
    fontSize: 14,
    color: '#999',
  },
});

export default HomeScreen;