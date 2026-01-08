import {Txt} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {StyleSheet, View} from 'react-native';
import {Card} from '../common/Card';
import {formatDate} from '../../utils/challenge';
import {VerificationItem} from './VerificationItem';
import {useMe} from '../../hooks/useMe';
import {ChallengeVerificationResponse} from '../../api/verifications';

interface VerificationGroupProps {
  date: string;
  verifications: ChallengeVerificationResponse[];
}

export function VerificationGroup({ date, verifications }: VerificationGroupProps) {
  const adaptive = useAdaptive();
  const { data: me } = useMe();

  return (
    <View style={{ paddingHorizontal: 16, marginVertical: 8 }}>
      <Card>
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
            isMine={me ? item.tossId === me.tossId : false}
          />
        ))}
      </Card>
    </View>
  );
}

const styles = StyleSheet.create({
  dateSection: {
    alignItems: 'center',
    marginBottom: 12,
  },
});
