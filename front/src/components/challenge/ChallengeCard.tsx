import { Spacing, useNavigation } from '@granite-js/react-native';
import { Badge, ListHeader, Top, Txt } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { StyleSheet, View } from 'react-native';

import { setSelectedChallenge } from '../../stores/challengeStore';
import {
  formatDate,
  getDDay,
  getChallengeStatusBadge,
  getNextScheduleMessage,
  getVerificationMessage,
  isTodayChallenge,
} from '../../utils/challenge';
import { Challenge } from './types';

export function ChallengeCard({ challenge }: { challenge: Challenge }) {
  const adaptive = useAdaptive();
  const navigation = useNavigation();

  const getBadges = (): any[] => {
    switch (challenge.status) {
      case 'PENDING':
        return [
          { label: getDDay(challenge.startAt), type: 'blue' as const, style: 'weak' as const },
          {
            label: `${Number(challenge.penaltyAmount).toLocaleString()}원`,
            type: 'blue' as const,
            style: 'weak' as const,
          },
        ];
      case 'COMPLETE':
        return [{ label: '종료됨', type: 'blue' as const, style: 'weak' as const }];
      default: // ACTIVE
        return [
          getChallengeStatusBadge(
            challenge.verificationStatus,
            challenge.daysOfWeek || [],
            Number(challenge.weeklyRequiredCount),
            challenge.weeklyProgressCount
          ),
          {
            label: `${challenge.weeklyProgressCount}/${challenge.weeklyRequiredCount}`,
            type: (challenge.verificationStatus === 'VERIFIED' ? 'green' : 'yellow') as 'green' | 'yellow',
            style: 'weak' as const,
          },
          {
            label: `${Number(challenge.penaltyAmount).toLocaleString()}원`,
            type: 'blue' as const,
            style: 'weak' as const,
          },
        ];
    }
  };

  const badges = getBadges();

  const getSubtitle = () => {
    if (challenge.status === 'PENDING') {
      return `시작일 : ${formatDate(challenge.startAt || '')}`;
    }
    if (challenge.status === 'COMPLETE') {
      return `종료일 : ${formatDate(challenge.endAt || '')}`;
    }
    if (challenge.status === 'ACTIVE' && challenge.verifyEnd) {
      return isTodayChallenge(
        challenge.daysOfWeek,
        Number(challenge.weeklyRequiredCount),
        challenge.weeklyProgressCount
      )
        ? getVerificationMessage(challenge.verifyStart, challenge.verifyEnd)
        : getNextScheduleMessage(challenge.daysOfWeek);
    }
    return undefined;
  };

  return (
    <View style={styles.challengeCard}>
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>{challenge.title}</Top.TitleParagraph>}
        subtitle1={<Top.SubtitleParagraph color={adaptive.grey600}>{getSubtitle()}</Top.SubtitleParagraph>}
        subtitle2={
          <View style={{ flexDirection: 'row', gap: 2, flexWrap: 'wrap' }}>
            {badges.map((badge, index) => (
              <Badge key={index} type={badge.type} badgeStyle={badge.style} size="small">
                {badge.label}
              </Badge>
            ))}
          </View>
        }
        right={
          <View style={{ flexDirection: 'column', alignItems: 'flex-end', justifyContent: 'center' }}>
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
            <Spacing size={6} />
            <ChallengeMembers members={challenge.members} />
          </View>
        }
      />
    </View>
  );
}

function ChallengeMembers({ members }: { members: Challenge['members'] }) {
  const adaptive = useAdaptive();

  if (members.length > 1) {
    const randomIndex = Math.floor(Math.random() * members.length);
    const randomMember = members[randomIndex];

    return (
      <Txt color={adaptive.grey600} typography="t7" numberOfLines={1} ellipsizeMode="tail" style={{ maxWidth: 100 }}>
        {`${randomMember.userName} 외 ${members.length - 1}명`}
      </Txt>
    );
  }
}

const styles = StyleSheet.create({
  challengeCard: {
    paddingHorizontal: 16,
  },
});
