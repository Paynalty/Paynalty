import { Txt } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { StyleSheet, View } from 'react-native';
import { formatDate } from '../../utils/challenge';
import { VerificationItem } from './VerificationItem';

interface VerificationData {
  id: number | string;
  userName: string;
  userAvatar?: string;
  imageUrl: string;
  dateTime: string;
}

interface VerificationGroupProps {
  date: string; // ISO string or common date string
  verifications: VerificationData[];
}

export function VerificationGroup({ date, verifications, imageHeight }: VerificationGroupProps & { imageHeight?: number }) {
  const adaptive = useAdaptive();

  return (
    <View style={styles.card}>
      <View style={styles.dateSection}>
        <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
          {formatDate(date)}
        </Txt>
      </View>

      {verifications.map((item, index) => (
        <VerificationItem
          key={item.id}
          userName={item.userName}
          userAvatar={item.userAvatar}
          imageUrl={item.imageUrl}
          dateTime={item.dateTime}
          showDivider={index > 0}
          imageHeight={imageHeight}
        />
      ))}
    </View>
  );
}

const styles = StyleSheet.create({
  card: {
    padding: 16,
    marginHorizontal: 16,
    marginVertical: 8,
    backgroundColor: '#ffffff',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#e0e0e0',
  },
  dateSection: {
    alignItems: 'center',
    marginBottom: 12,
  },
});
