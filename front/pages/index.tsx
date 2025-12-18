import {createRoute, Spacing} from '@granite-js/react-native';
import {View, StyleSheet, ScrollView} from 'react-native';
import {Asset, Txt, Top, ListRow, Border, ListHeader, TextButton, Icon} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {useState} from 'react';

export const Route = createRoute('/', {
    component: Page,
});

// 챌린지 데이터 타입 정의
type ChallengeStatus = 'completed' | 'in_progress' | 'pending';

type Challenge = {
    id: string;
    title: string;
    status: ChallengeStatus;
    currentCount: number;
    totalCount: number;
    penaltyAmount: number;
    participants: string;
    remainingTime?: string;
};

function Page() {
    const adaptive = useAdaptive();

    // Mock 데이터 - 추후 API로 교체
    const [challenges] = useState<Challenge[]>([
        {
            id: '1',
            title: '운동 30분 챌린지',
            status: 'completed',
            currentCount: 3,
            totalCount: 3,
            penaltyAmount: 5000,
            participants: '민수 · 지은 · 지영 ··',
        },
        {
            id: '2',
            title: '매일 만보 걷기',
            status: 'in_progress',
            currentCount: 3,
            totalCount: 7,
            penaltyAmount: 5000,
            participants: '하트브레이커',
            remainingTime: '5시간 31분',
        },
    ]);

    return (
        <ScrollView style={styles.container} contentContainerStyle={styles.content}>
            {/* 오늘의 미션 */}
            <View style={{
                backgroundColor: adaptive.blue500,
                borderBottomLeftRadius: 20,
                borderBottomRightRadius: 20,
                overflow: 'hidden'
            }}>
                <Top
                    title={
                        <Top.TitleSelector color={adaptive.background}>
                            오늘의 미션
                        </Top.TitleSelector>
                    }
                    subtitle2={
                        <Top.SubtitleParagraph color={adaptive.background}>
                            매일 만보 걷기{'\n'}
                            인증 가능 시간 : 00:00 ~ 23:00
                        </Top.SubtitleParagraph>
                    }
                    right={
                        <Asset.Image
                            frameShape={Asset.frameShape.CleanW60}
                            source={{
                                uri: 'https://static.toss.im/ml-product/typing-laptop-apng.png',
                            }}
                        />
                    }
                />
            </View>

            <Spacing size={20}/>

            {/* 목표 설정 섹션 */}
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        목표를 정하고{'\n'}
                        친구들과 달성해보세요!
                    </Top.TitleParagraph>
                }
                right={<Top.RightButton>만들기</Top.RightButton>}
            />

            <ListRow
                left={<ListRow.Icon name="icon-emoji-money-with-wings"/>}
                contents={
                    <ListRow.Texts
                        type="2RowTypeD"
                        top="오늘 미션을 하지 않으면"
                        topProps={{color: adaptive.grey600}}
                        bottom="5000원을 납부해야 돼요"
                        bottomProps={{color: adaptive.blue500, fontWeight: 'bold'}}
                    />
                }
                right={<Icon name="icon-x-mono" color={adaptive.grey600} size={16}/>}
                verticalPadding={16}
            />
            <Border/>

            {/* 진행중인 챌린지 헤더 */}
            <ListHeader
                title={
                    <ListHeader.TitleSelector
                        typography="t4"
                        color={adaptive.grey600}
                        fontWeight="bold"
                    >
                        진행중인 챌린지
                    </ListHeader.TitleSelector>
                }
            />

            {/* 챌린지 카드 반복 렌더링 */}
            {challenges.map((challenge, index) => (
                <View key={challenge.id}>
                    {index > 0 && <Spacing size={16}/>}
                    <ChallengeCard challenge={challenge}/>
                </View>
            ))}
        </ScrollView>
    );
}

// 챌린지 카드 컴포넌트

const styles = StyleSheet.create({
    container: {
        flex: 1,
        backgroundColor: 'white',
    },
    content: {
        paddingVertical: 20,
    },
    header: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        paddingHorizontal: 16,
        paddingVertical: 10,
        height: 44,
    },
    headerLeft: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 8,
    },
    titleContainer: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 5,
    },
    headerRight: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 4,
    },
    iconButton: {
        width: 44,
        height: 44,
        justifyContent: 'center',
        alignItems: 'center',
    },
    challengeCard: {
        paddingHorizontal: 16,
    },
});