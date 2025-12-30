import { View, StyleSheet } from 'react-native';
import { Spacing, useNavigation } from '@granite-js/react-native';
import { Badge, Top, ListHeader } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';

import { Challenge, ChallengeStatus } from './types';
import { setSelectedChallenge } from '../../stores/challengeStore';
import { getVerificationMessage, isTodayChallenge, getNextScheduleMessage } from '../../utils/challenge';

export function ChallengeCard({ challenge }: { challenge: Challenge }) {
  const adaptive = useAdaptive();
  const navigation = useNavigation();

  const getStatusBadge = (status: ChallengeStatus) => {
    switch (status) {
      case 'COMPLETE':
        return { label: '인증 완료', type: 'green' as const, style: 'weak' as const };
      case 'ACTIVE':
        return { label: '지금 할 차례에요', type: 'yellow' as const, style: 'weak' as const };
      case 'PENDING':
        return { label: '대기중', type: 'blue' as const, style: 'weak' as const };
      default:
        return { label: '대기중', type: 'blue' as const, style: 'weak' as const };
    }
  };

  const badges = [
    getStatusBadge(challenge.status),
    {
      label: `${challenge.weeklyProgressCount}/${challenge.weeklyRequiredCount}`,
      type: (challenge.status === 'COMPLETE' ? 'green' : 'yellow') as any,
      style: 'weak' as const,
    },
    { label: `${challenge.penaltyAmount}원`, type: 'blue' as any, style: 'weak' as const },
  ];

  return (
    <View style={styles.challengeCard}>
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>{challenge.title}</Top.TitleParagraph>}
        subtitle1={
          challenge.verifyEnd ? (
            <Top.SubtitleParagraph>
              {isTodayChallenge(
                challenge.daysOfWeek,
                Number(challenge.weeklyRequiredCount),
                challenge.weeklyProgressCount
              )
                ? getVerificationMessage(challenge.verifyStart, challenge.verifyEnd)
                : getNextScheduleMessage(challenge.daysOfWeek)}
            </Top.SubtitleParagraph>
          ) : undefined
        }
        subtitle2={<Top.SubtitleBadges items={badges} />}
        right={
          <View style={{ flexDirection: 'column', alignItems: 'flex-end' }}>
            <View>
              <ListHeader.RightArrow
                typography="t7"
                color={adaptive.blue500}
                onPress={() => {
                  setSelectedChallenge(challenge);
                  navigation.navigate('/challenge-detail');
                }}
              >
                자세히 보기
              </ListHeader.RightArrow>
            </View>
            <Spacing size={8} />

            <View>
              <Badge size="small" type="blue" badgeStyle="weak">
                {challenge.participants}
              </Badge>
            </View>
          </View>
        }
      />
    </View>
  );
}

const styles = StyleSheet.create({
  challengeCard: {
    paddingHorizontal: 16,
  },
});
