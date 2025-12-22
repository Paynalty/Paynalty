import {
    Asset, Txt, Top, ListHeader, Post, BarChart, List, ListRow, FixedBottomCTA, FixedBottomCTAProvider, Spacing
} from '@toss/tds-react-native';
import {Paragraph, useAdaptive } from '@toss/tds-react-native/private';
import {createRoute} from "@granite-js/react-native";
import {Pressable, ScrollView} from 'react-native';
import {getSelectedChallenge} from '../../src/stores/challengeStore';

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
            <Pressable onPress={() => navigation.goBack()}>
                <Asset.Icon
                    frameShape={{ width: 24, height: 24 }}
                    name="icon-arrow-back-ios-mono"
                    color="adaptive-grey-900"
                    accessibilityLabel="뒤로 가기"
                />
            </Pressable>
            <Spacing size={16} />
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
                    <Top.RightAssetContent
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
                title={
                    <Top.TitleParagraph color={adaptive.grey900}></Top.TitleParagraph>
                }
                subtitle1={
                    <Top.SubtitleParagraph>남은 시간 : 5시간 31분</Top.SubtitleParagraph>
                }
                subtitle2={
                    <Top.SubtitleBadges
                        items={[
                            { text: '지금 할 차례에요', type: 'yellow', style: 'weak' },
                            { text: '3/7', type: 'yellow', style: 'weak' },
                            { text: '5000원', type: 'blue', style: 'weak' },
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
                    <ListHeader.RightArrow color={adaptive.grey400}>
                        자세히 보기
                    </ListHeader.RightArrow>
                }
            />
            <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
                2025년 8월 15일 월요일
            </Txt>
            <Post.H2 paddingBottom={8}>
                <Paragraph.Text>지은</Paragraph.Text>
            </Post.H2>
            <>
                <Asset.Image
                    frameShape={Asset.frameShape.CleanW32}
                    source={{
                        uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png',
                    }}
                />
            </>
            <>
                <Asset.Icon
                    frameShape={{ width: 250 }}
                    name="icon-document-folder-yellow"
                />
            </>
            <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                이의제기
            </Txt>
            <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                오전 10:58
            </Txt>
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
            />
            <ListHeader
                title={
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
                    { xAxisLabel: '나', value: 2 },
                    { xAxisLabel: '길동', value: 3 },
                    { xAxisLabel: '형욱', value: 3 },
                    { xAxisLabel: '지은', value: 2 },
                    { xAxisLabel: '은채', value: 1 },
                ]}
                fill={{ type: 'single-bar', theme: 'blue', barIndex: 4 }}
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
                    <ListHeader.RightArrow color={adaptive.grey400}>
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
                    <ListHeader.RightArrow color={adaptive.grey400}>
                        친구 초대하기
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
                    <ListHeader.RightArrow color={adaptive.grey400}>
                        수정하기
                    </ListHeader.RightArrow>
                }
            />
            <List rowSeparator="none">
                <ListRow
                    left={<ListRow.LeftText marginTop={0}>언제</ListRow.LeftText>}
                    contents={
                        <ListRow.Texts
                            type="1RowTypeA"
                            top=""
                            topProps={{ color: adaptive.grey700 }}
                        />
                    }
                    right={
                        <ListRow.RightTexts
                            type="1RowTypeA"
                            top="14:00~ 23:00"
                            topProps={{ color: adaptive.grey700 }}
                        />
                    }
                    verticalPadding="large"
                />
                <ListRow
                    left={<ListRow.LeftText marginTop={0}>주기</ListRow.LeftText>}
                    contents={
                        <ListRow.Texts
                            type="1RowTypeA"
                            top=""
                            topProps={{ color: adaptive.grey700 }}
                        />
                    }
                    right={
                        <ListRow.RightTexts
                            type="1RowTypeA"
                            top="월, 화, 목"
                            topProps={{ color: adaptive.grey700 }}
                        />
                    }
                    verticalPadding="large"
                />
                <ListRow
                    left={<ListRow.LeftText marginTop={0}>방법</ListRow.LeftText>}
                    contents={
                        <ListRow.Texts
                            type="1RowTypeA"
                            top=""
                            topProps={{ color: adaptive.grey700 }}
                        />
                    }
                    right={
                        <ListRow.RightTexts
                            type="1RowTypeA"
                            top="사진 인증"
                            topProps={{ color: adaptive.grey700 }}
                        />
                    }
                    verticalPadding="large"
                />
                <ListRow
                    left={<ListRow.LeftText marginTop={0}>패널티</ListRow.LeftText>}
                    contents={
                        <ListRow.Texts
                            type="1RowTypeA"
                            top=""
                            topProps={{ color: adaptive.grey700 }}
                        />
                    }
                    right={
                        <ListRow.RightTexts
                            type="1RowTypeA"
                            top="5000원"
                            topProps={{ color: adaptive.grey700 }}
                        />
                    }
                    verticalPadding="large"
                />
            </List>
            <FixedBottomCTAProvider>
                <FixedBottomCTA loading={false} bottomAccessory="로그인 없이 둘러보기">
                    바로 인증하기
                </FixedBottomCTA>
            </FixedBottomCTAProvider>
        </ScrollView>
    );
}