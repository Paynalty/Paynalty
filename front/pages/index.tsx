import {createRoute, Spacing} from '@granite-js/react-native';
import {View, StyleSheet, ScrollView, Pressable, Text} from 'react-native';
import {Asset, Top, ListRow, Border, ListHeader, Icon} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {useState, useEffect, useMemo} from 'react';
import {Storage} from '@apps-in-toss/framework';
import {ChallengeCard} from 'components/challenge/ChallengeCard';
import {getChallenges} from '../src/stores/challengeStore';

export const Route = createRoute('/', {
    component: Page,
});

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();

    useEffect(() => {
        checkOnboarding();
    }, []);

    const checkOnboarding = async () => {
        try {
            // 테스트용: 저장된 온보딩 상태 삭제
            // await Storage.removeItem('hasCompletedOnboarding');

            const hasCompletedOnboarding = await Storage.getItem('hasCompletedOnboarding');
            if (!hasCompletedOnboarding) {
                navigation.navigate('/auth');
            }
        } catch (error) {
            console.error('Failed to check onboarding status:', error);
        }
    };

    const [showTooltip, setShowTooltip] = useState(true);
    const [isMissionExpanded, setIsMissionExpanded] = useState(false);

    // TODO : Mock 데이터를 store에서 조회 - 추후 API로 교체
    const challenges = getChallenges();

    // 오늘 미션 필터링 (remainingTime이 있는 챌린지)
    const todayMissions = useMemo(
        () => challenges.filter(challenge => challenge.remainingTime !== undefined),
        [challenges]
    );

    // 오늘 미션 벌금 합산
    const totalPenalty = useMemo(
        () => todayMissions.reduce((sum, challenge) => sum + challenge.penaltyAmount, 0),
        [todayMissions]
    );

    return (
        <ScrollView style={styles.container} contentContainerStyle={styles.content}>
            {/* 오늘의 미션 */}
            {/*TODO : 로그인 안했을 때, 로그인 했을 때 , 미션이 없을 때로 구분*/}
            <Pressable onPress={() => setIsMissionExpanded(!isMissionExpanded)}>
                <View style={{
                    backgroundColor: adaptive.blue500,
                    borderBottomLeftRadius: 20,
                    borderBottomRightRadius: 20,
                    overflow: 'hidden'
                }}>
                    <Top
                        title={
                            <View style={{flexDirection: 'row', alignItems: 'center', gap: 4}}>
                                <Top.TitleParagraph color={adaptive.background}>
                                    오늘의 미션
                                </Top.TitleParagraph>
                                {todayMissions.length > 0 && (
                                    <Text style={{color: adaptive.background, fontSize: 16, fontWeight: 'bold'}}>
                                        {isMissionExpanded ? '∨' : '>'}
                                    </Text>
                                )}

                            </View>
                        }
                        subtitle2={
                            todayMissions.length > 0 ? (
                                isMissionExpanded ? (
                                    // 확장 상태: 모든 미션 표시
                                    <View>
                                        {todayMissions.map((mission, index) => (
                                            <View key={mission.id}>
                                                {index > 0 && <Spacing size={8} />}
                                                <Top.SubtitleParagraph color={adaptive.background}>
                                                    {mission.title}{'\n'}
                                                    남은 시간 : {mission.remainingTime}
                                                </Top.SubtitleParagraph>
                                            </View>
                                        ))}
                                    </View>
                                ) : (
                                    // 축소 상태: 첫 번째 미션만
                                    <Top.SubtitleParagraph color={adaptive.background}>
                                        {todayMissions[0]?.title} {'\n'}
                                        남은 시간 : {todayMissions[0]?.remainingTime}
                                    </Top.SubtitleParagraph>
                                )
                            ) : undefined
                        }
                        right={
                            <View style={{flexDirection: 'row', alignItems: 'center', gap: 8}}>
                                {todayMissions.length > 0 && (
                                    <Asset.Image
                                        frameShape={Asset.frameShape.CleanW60}
                                        source={{
                                            uri: 'https://static.toss.im/ml-product/typing-laptop-apng.png',
                                        }}
                                    />
                                )}
                            </View>
                        }
                    />
                </View>
            </Pressable>

            <Spacing size={20}/>

            {/* 목표 설정 섹션 */}
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        목표를 정하고{'\n'}
                        친구들과 달성해보세요!
                    </Top.TitleParagraph>
                }
                right={
                    <Top.RightButton onPress={() => navigation.navigate('/create-goal')}>
                        만들기
                    </Top.RightButton>
                }
            />

            {
                showTooltip && todayMissions.length > 0 && (
                    <>
                        <ListRow
                            left={<ListRow.Icon name="icon-emoji-money-with-wings"/>}
                            contents={
                                <ListRow.Texts
                                    type="2RowTypeD"
                                    top="오늘 미션을 하지 않으면"
                                    topProps={{color: adaptive.grey600}}
                                    bottom={`${totalPenalty.toLocaleString()}원을 납부해야 돼요`}
                                    bottomProps={{color: adaptive.blue500, fontWeight: 'bold'}}
                                />
                            }
                            right={
                                <Pressable onPress={() => setShowTooltip(false)}>
                                    <Icon name="icon-x-mono" color={adaptive.grey600} size={16}/>
                                </Pressable>
                            }
                            verticalPadding={16}
                        />
                    </>
                )
            }

            {/* 진행중인 챌린지 헤더 */
            }
            {/*TODO : 예정된 챌린지, 완료된 챌린지 구분하여 추가*/
            }
            <ListHeader
                title={
                    <ListHeader.TitleSelector
                        typography="t4"
                        color={adaptive.grey800}
                        fontWeight="bold"
                    >
                        진행중인 챌린지
                    </ListHeader.TitleSelector>
                }
            />

            {/* 챌린지 카드 반복 렌더링 */
            }
            {/*TODO : 무한 스크롤 or  페이징 적용*/
            }
            {
                challenges.map((challenge, index) => (
                    <View key={challenge.id}>
                        {index > 0 && <Spacing size={16}/>}
                        <ChallengeCard challenge={challenge}/>
                    </View>
                ))
            }
        </ScrollView>
    )
        ;
}

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
});