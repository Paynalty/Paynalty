import { useAdaptive } from '@toss/tds-react-native/private';
import { Platform, StyleSheet, View, ViewProps } from 'react-native';

interface CardProps extends ViewProps {
  padding?: number;
}

export function Card({ children, style, padding = 20, ...props }: CardProps) {
  const adaptive = useAdaptive();

  return (
    <View
      style={[
        styles.card,
        {
          backgroundColor: adaptive.background,
          padding,
        },
        style,
      ]}
      {...props}
    >
      {children}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    borderRadius: 16,
    ...Platform.select({
      ios: {
        shadowColor: '#000',
        shadowOffset: { width: 0, height: 2 },
        shadowOpacity: 0.05,
        shadowRadius: 8,
      },
      android: {
        elevation: 2,
      },
    }),
  },
});
