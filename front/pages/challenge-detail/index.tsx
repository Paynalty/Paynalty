import {
    Asset, Txt, ListHeader, FixedBottomCTA, FixedBottomCTAProvider, Top, BarChart
} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {createRoute, Spacing} from "@granite-js/react-native";
import {Pressable, ScrollView, StyleSheet, View} from 'react-native';
import {getSelectedChallenge, setSelectedChallengeId} from '../../src/stores/challengeStore';
import {useVerificationModal} from '../../src/hooks/useVerificationModal';

export const Route = createRoute('/challenge-detail', {
    component: Page,
})

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    const selectedChallenge = getSelectedChallenge();
    const {open: openVerificationModal} = useVerificationModal();

    if (!selectedChallenge) {
        return (
            <Txt color={adaptive.grey600}>챌린지 정보를 불러올 수 없습니다.</Txt>
        );
    }

    return (
        <ScrollView>
            <Spacing size={16}/>
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        매일 만보 걷기
                    </Top.TitleParagraph>
                }
                subtitle2={
                    <Top.SubtitleParagraph color={adaptive.grey600}>
                        하트브레이커 6명과 도전 중
                    </Top.SubtitleParagraph>
                }
                right={
                    <Top.UpperAssetContent
                        content={
                            <Asset.Image
                                frameShape={Asset.frameShape.CleanW60}
                                source={{
                                    uri: 'https://static.toss.im/ml-product/observer-binocular.png',
                                }}
                            />
                        }
                    />
                }
                lowerGap={0}
            />
            <Top
                subtitle1={
                    <Top.SubtitleParagraph>남은 시간 : 5시간 31분</Top.SubtitleParagraph>
                }
                subtitle2={
                    <Top.SubtitleBadges
                        items={[
                            {label: '지금 할 차례에요', type: 'yellow', style: 'weak'},
                            {label: '3/7', type: 'yellow', style: 'weak'},
                            {label: '5000원', type: 'blue', style: 'weak'},
                        ]}
                    />
                }
                upperGap={0}
            />
            <ListHeader
                title={
                    <ListHeader.TitleParagraph
                        color={adaptive.grey800}
                        fontWeight="bold"
                        typography="t5"
                    >
                        최근 인증 현황
                    </ListHeader.TitleParagraph>
                }
                right={
                    <Pressable onPress={() => navigation.navigate('/challenge-detail/verification-history')}>
                        <ListHeader.RightArrow
                            typography="t7"
                            color={adaptive.grey600}>
                            자세히 보기
                        </ListHeader.RightArrow>
                    </Pressable>
                }
            />
            <View style={styles.verificationCard}>
                <View style={styles.dateSection}>
                    <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
                        2025년 8월 15일 월요일
                    </Txt>
                </View>
                <View style={styles.userSection}>
                    <Asset.Image
                        frameShape={{width: 32, height: 32}}
                        source={{uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                    />
                    <Txt color={adaptive.grey700} typography="t5" fontWeight="bold">
                        지은
                    </Txt>
                </View>
                <View style={styles.imageSection}>
                    <Asset.Image
                        frameShape={{width: 300, height: 300}}
                        source={{uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                    />
                </View>
                <View style={styles.bottomSection}>
                    <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                        이의제기
                    </Txt>
                    <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                        오전 10:58
                    </Txt>
                </View>
            </View>
            <ListHeader
                title={
                    <ListHeader.TitleParagraph
                        color={adaptive.grey800}
                        fontWeight="bold"
                        typography="t5"
                    >
                        주간 인증 현황
                    </ListHeader.TitleParagraph>
                }
                right={
                    <ListHeader.TitleSelector
                        typography="t7"
                        color={adaptive.grey800}
                        fontWeight="regular"
                    >
                        보기 기준
                    </ListHeader.TitleSelector>
                }
            />
            <BarChart
                data={[
                    {xAxisLabel: '나', value: 2},
                    {xAxisLabel: '길동', value: 3},
                    {xAxisLabel: '형욱', value: 3},
                    {xAxisLabel: '지은', value: 2},
                    {xAxisLabel: '은채', value: 1},
                ]}
                fill={{type: 'all-bar', theme: 'blue'}}
            />
            <ListHeader
                title={
                    <ListHeader.TitleParagraph
                        color={adaptive.grey800}
                        fontWeight="bold"
                        typography="t5"
                    >
                        챌린저 규칙
                    </ListHeader.TitleParagraph>
                }
                right={
                    <ListHeader.RightArrow
                        typography="t7"
                        color={adaptive.grey600}>
                        수정하기
                    </ListHeader.RightArrow>
                }
            />
            <View style={styles.rulesCard}>
                <View style={styles.gridCell}>
                    <Asset.Icon
                        frameShape={{width: 28, height: 28}}
                        name="icon-calendar-gradient-mono"
                        color={adaptive.grey600}
                    />
                    <Txt color={adaptive.grey600} typography="t6" fontWeight="medium">
                        마감일
                    </Txt>
                    <Txt color={adaptive.grey900} typography="t5" fontWeight="bold">
                        2025년 1월 15일
                    </Txt>
                </View>
                <View style={styles.gridRow}>
                    <View style={styles.gridCell}>
                        <Asset.Icon
                            frameShape={{width: 24, height: 24}}
                            name="icon-clock-mono"
                            color={adaptive.grey600}
                        />
                        <Txt color={adaptive.grey600} typography="t7" fontWeight="medium">
                            인증 시간
                        </Txt>
                        <Txt color={adaptive.grey900} typography="t6" fontWeight="semibold">
                            14:00 ~ 23:00
                        </Txt>
                    </View>
                    <View style={styles.gridDivider}/>
                    <View style={styles.gridCell}>
                        <Asset.Icon
                            frameShape={{width: 24, height: 24}}
                            name="icon-repeat-mono"
                            color={adaptive.grey600}
                        />
                        <Txt color={adaptive.grey600} typography="t7" fontWeight="medium">
                            인증 주기
                        </Txt>
                        <Txt color={adaptive.grey900} typography="t6" fontWeight="semibold">
                            월, 화, 목 / 3회
                        </Txt>
                    </View>
                </View>
                <View style={styles.gridHorizontalDivider}/>
                <View style={styles.gridRow}>
                    <View style={styles.gridCell}>
                        <Asset.Icon
                            frameShape={{width: 24, height: 24}}
                            name="icon-camera-mono"
                            color={adaptive.grey600}
                        />
                        <Txt color={adaptive.grey600} typography="t7" fontWeight="medium">
                            인증 방법
                        </Txt>
                        <Txt color={adaptive.grey900} typography="t6" fontWeight="semibold">
                            사진
                        </Txt>
                    </View>
                    <View style={styles.gridDivider}/>
                    <View style={styles.gridCell}>
                        <Asset.Icon
                            frameShape={{width: 24, height: 24}}
                            name="icon-won-mono"
                            color={adaptive.grey600}
                        />
                        <Txt color={adaptive.grey600} typography="t7" fontWeight="medium">
                            패널티
                        </Txt>
                        <Txt color={adaptive.grey900} typography="t5" fontWeight="bold">
                            5000원
                        </Txt>
                    </View>
                </View>
            </View>
            <ListHeader
                title={
                    <ListHeader.TitleParagraph
                        color={adaptive.grey800}
                        fontWeight="bold"
                        typography="t5"
                    >
                        패널티 이력 보기
                    </ListHeader.TitleParagraph>
                }
                right={
                    <ListHeader.RightArrow
                        typography="t7"
                        color={adaptive.grey600}>
                        자세히 보기
                    </ListHeader.RightArrow>
                }
            />
            <ListHeader
                title={
                    <ListHeader.TitleParagraph
                        color={adaptive.grey800}
                        fontWeight="bold"
                        typography="t5"
                    >
                        참여중인 친구
                    </ListHeader.TitleParagraph>
                }
                right={
                    <ListHeader.RightArrow
                        typography="t7"
                        color={adaptive.grey600}>
                        자세히 보기
                    </ListHeader.RightArrow>
                }
            />
            <FixedBottomCTAProvider>
                <FixedBottomCTA loading={false} onPress={openVerificationModal}>
                    바로 인증하기
                </FixedBottomCTA>
            </FixedBottomCTAProvider>
        </ScrollView>
    );
}

const styles = StyleSheet.create({
    verificationCard: {
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
    userSection: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 8,
        marginBottom: 16,
    },
    imageSection: {
        alignItems: 'center',
        marginVertical: 16,
    },
    bottomSection: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginTop: 12,
    },
    rulesCard: {
        padding: 0,
        marginHorizontal: 16,
        marginVertical: 8,
        overflow: 'hidden',
    },
    gridRow: {
        flexDirection: 'row',
        alignItems: 'stretch',
    },
    gridCell: {
        flex: 1,
        alignItems: 'center',
        justifyContent: 'center',
        paddingVertical: 20,
        paddingHorizontal: 12,
        gap: 6,
    },
    gridDivider: {
        width: 1,
    },
    gridHorizontalDivider: {
        height: 1,
    },
});
