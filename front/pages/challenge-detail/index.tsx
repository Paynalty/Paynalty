import {
    Asset, Txt, ListHeader, List, ListRow, FixedBottomCTA, FixedBottomCTAProvider, Top, TextButton, BarChart
} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {createRoute, Spacing} from "@granite-js/react-native";
import {ScrollView, StyleSheet, View} from 'react-native';
import {getSelectedChallenge, setSelectedChallengeId} from '../../src/stores/challengeStore';

export const Route = createRoute('/challenge-detail', {
    component: Page,
})

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    const selectedChallenge = getSelectedChallenge();

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
                    <ListHeader.RightArrow
                        typography="t7"
                        color={adaptive.grey600}>
                        자세히 보기
                    </ListHeader.RightArrow>
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
                        source={{ uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png' }}
                    />
                    <Txt color={adaptive.grey700} typography="t5" fontWeight="bold">
                        지은
                    </Txt>
                </View>
                <View style={styles.imageSection}>
                    <Asset.Image
                        frameShape={{width: 300, height: 300}}
                        source={{ uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png' }}
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
            <List rowSeparator="none">
                <ListRow
                    left={<ListRow.LeftText color={adaptive.blue600}>언제</ListRow.LeftText>}
                    contents={
                        <ListRow.Texts
                            type="1RowTypeA"
                            top=""
                        />
                    }
                    right={
                        <ListRow.RightTexts
                            type="1RowTypeA"
                            top="14:00~ 23:00"
                        />
                    }
                    verticalPadding="large"
                />
                <ListRow
                    left={<ListRow.LeftText color={adaptive.blue600}>주기</ListRow.LeftText>}
                    contents={
                        <ListRow.Texts
                            type="1RowTypeA"
                            top=""
                        />
                    }
                    right={
                        <ListRow.RightTexts
                            type="1RowTypeA"
                            top="월, 화, 목"
                        />
                    }
                    verticalPadding="large"
                />
                <ListRow
                    left={<ListRow.LeftText color={adaptive.blue600}>방법</ListRow.LeftText>}
                    contents={
                        <ListRow.Texts
                            type="1RowTypeA"
                            top=""
                        />
                    }
                    right={
                        <ListRow.RightTexts
                            type="1RowTypeA"
                            top="사진 인증"
                        />
                    }
                    verticalPadding="large"
                />
                <ListRow
                    left={<ListRow.LeftText color={adaptive.blue600}>패널티</ListRow.LeftText>}
                    contents={
                        <ListRow.Texts
                            type="1RowTypeA"
                            top=""
                        />
                    }
                    right={
                        <ListRow.RightTexts
                            type="1RowTypeA"
                            top="5000원"
                        />
                    }
                    verticalPadding="large"
                />
            </List>
            <FixedBottomCTAProvider>
                <FixedBottomCTA loading={false}>
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
});
