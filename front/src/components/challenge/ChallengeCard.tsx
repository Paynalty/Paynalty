import {View, StyleSheet} from 'react-native';
import {Spacing, useNavigation} from '@granite-js/react-native';
import {Badge, Top, TextButton} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {Challenge, ChallengeStatus} from './types';
import {setSelectedChallengeId} from '../../stores/challengeStore';

export function ChallengeCard({challenge}: { challenge: Challenge }) {
    const adaptive = useAdaptive();
    const navigation = useNavigation();

    // 상태에 따른 배지 생성
    const getStatusBadge = (status: ChallengeStatus) => {
        switch (status) {
            case 'completed':
                return {label: '인증 완료', type: 'green' as const, style: 'weak' as const};
            case 'in_progress':
                return {label: '지금 할 차례에요', type: 'yellow' as const, style: 'weak' as const};
            case 'pending':
                return {label: '대기중', type: 'blue' as const, style: 'weak' as const};
        }
    };

    const badges = [
        getStatusBadge(challenge.status),
        {
            label: `${challenge.currentCount}/${challenge.totalCount}`,
            type: challenge.status === 'completed' ? 'green' : 'yellow',
            style: 'weak' as const
        },
        {label: `${challenge.penaltyAmount}원`, type: 'blue' as const, style: 'weak' as const},
    ];

    return (
        <View style={styles.challengeCard}>
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        {challenge.title}
                    </Top.TitleParagraph>
                }
                subtitle1={
                    challenge.remainingTime ? (
                        <Top.SubtitleParagraph>
                            남은 시간 : {challenge.remainingTime}
                        </Top.SubtitleParagraph>
                    ) : undefined
                }
                subtitle2={
                    <Top.SubtitleBadges items={badges}/>
                }
                right={
                    <View style={{alignItems: "flex-end"}}>
                        <TextButton
                            variant="arrow"
                            typography="t5"
                            color={adaptive.blue500}
                            fontWeight="semibold"
                            onPress={() => {
                                setSelectedChallengeId(challenge.id);
                                navigation.navigate('/challenge-detail');
                            }}
                        >
                            자세히 보기
                        </TextButton>

                        <Spacing size={8}/>

                        <Badge size="small" type="blue" badgeStyle="weak">
                            {challenge.participants}
                        </Badge>
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
