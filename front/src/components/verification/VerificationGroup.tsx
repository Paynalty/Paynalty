import { Txt } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { StyleSheet, View } from 'react-native';
import { formatDate } from '../../utils/challenge';
import { VerificationItem } from './VerificationItem';
import { useMe } from '../../hooks/useMe';
import { ChallengeVerificationResponse } from '../../api/verifications';

interface VerificationGroupProps {
  date: string;
  verifications: ChallengeVerificationResponse[];
}

export function VerificationGroup({ date, verifications }: VerificationGroupProps) {
  const adaptive = useAdaptive();
  const { data: me } = useMe();

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
          verificationId={item.id}
          userName={item.userName}
          imageUrl={item.imageUrl}
          dateTime={item.dateTime}
          showDivider={index !== 0}
          isMine={me ? item.userId === me.id : false}
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
