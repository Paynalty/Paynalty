import { Spacing, useNavigation } from '@granite-js/react-native';
import { Badge, ListHeader, Top } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { StyleSheet, View } from 'react-native';

import { setSelectedChallenge } from '../../stores/challengeStore';
import {
  getChallengeStatusBadge,
  getNextScheduleMessage,
  getVerificationMessage,
  isTodayChallenge,
} from '../../utils/challenge';
import { Challenge } from './types';

export function ChallengeCard({ challenge }: { challenge: Challenge }) {
  const adaptive = useAdaptive();
  const navigation = useNavigation();

  const badges = [
    getChallengeStatusBadge(
      challenge.verificationStatus,
      challenge.daysOfWeek,
      Number(challenge.weeklyRequiredCount),
      challenge.weeklyProgressCount
    ),
    {
      label: `${challenge.weeklyProgressCount}/${challenge.weeklyRequiredCount}`,
      type: (challenge.verificationStatus === 'VERIFIED' ? 'green' : 'yellow') as 'green' | 'yellow',
      style: 'weak' as const,
    },
    { label: `${challenge.penaltyAmount}원`, type: 'blue' as const, style: 'weak' as const },
  ];

  return (
    <View style={styles.challengeCard}>
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>{challenge.title}</Top.TitleParagraph>}
        subtitle1={
          challenge.status === 'ACTIVE' && challenge.verifyEnd ? (
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
        subtitle2={challenge.status === 'ACTIVE' ? <Top.SubtitleBadges items={badges} /> : undefined}
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
