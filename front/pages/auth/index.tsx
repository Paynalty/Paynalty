import {Asset, Txt, Top, List, ListRow, FixedBottomCTA, FixedBottomCTAProvider} from '@toss/tds-react-native';
import { Spacing } from '@granite-js/react-native';
import { useAdaptive } from '@toss/tds-react-native/private';

export default function Page() {
    const adaptive = useAdaptive();
    return (
        <>
            <>
                <Asset.Icon
                    frameShape={{ width: 24, height: 24 }}
                    name="icon-arrow-back-ios-mono"
                    color="adaptive-grey-900"
                    accessibilityLabel=""
                />
            </>
            <Txt typography="t6" fontWeight="semibold">
                페이널티
            </Txt>
            <>
                <Asset.Icon
                    frameShape={{ width: 20, height: 20 }}
                    name="icon-heart-mono"
                    color="adaptive-grey-opacity-600"
                    accessibilityLabel=""
                />
            </>
            <>
                <Asset.Icon
                    frameShape={{ width: 20, height: 20 }}
                    name="icon-dots-mono"
                    color="adaptive-grey-opacity-600"
                    accessibilityLabel=""
                />
            </>
            <>
                <Asset.Icon
                    frameShape={{ width: 20, height: 20 }}
                    name="icon-x-mono"
                    color="adaptive-grey-opacity-600"
                    accessibilityLabel=""
                />
            </>
            <>
                <Asset.Image
                    frameShape={{ width: 16, height: 16 }}
                    source={{
                        uri: 'https://static.toss.im/appsintoss/11149/88b088d4-24ea-4d39-9503-55e53608f7f1.png',
                    }}
                    accessibilityLabel=""
                />
            </>
            <Spacing size={14} />
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        못지키면, 돈이 나갑니다!
                    </Top.TitleParagraph>
                }
                subtitle2={
                    <Top.SubtitleParagraph>
                        목표 달성에 실패하면 정해둔 패널티가 발생해요.
                    </Top.SubtitleParagraph>
                }
                right={
                    <Top.UpperAssetContent
                        content={
                            <Asset.Lottie
                                frameShape={Asset.frameShape.CleanW60}
                                src="https://static.toss.im/lotties-common/siren-2-spot.json"
                            />
                        }
                    />
                }
            />
            <>
                <Asset.Image
                    frameShape={{ width: 250, height: 250 }}
                    source={{ uri: 'https://static.toss.im/ml-product/dog-a-coin.png' }}
                    accessibilityLabel=""
                />
            </>
            <List rowSeparator="none">
                <ListRow
                    left={
                        <ListRow.Image
                            source={{
                                uri: 'https://static.toss.im/ml-product/man-collar-box-red.png',
                            }}
                            hideBorder={true}
                        />
                    }
                    contents={
                        <ListRow.Texts
                            type="1RowTypeB"
                            top="친구와 함께 목표를 정하세요"
                            topProps={{ color: adaptive.grey800 }}
                        />
                    }
                    verticalPadding="large"
                />
                <ListRow
                    left={
                        <ListRow.Image
                            type="square"
                            source={{
                                uri: 'https://static.toss.im/ml-product/man-collar-coin-nvidia.png',
                            }}
                            hideBorder={true}
                        />
                    }
                    contents={
                        <ListRow.Texts
                            type="1RowTypeB"
                            top="실패하면 벌금이 기록돼요"
                            topProps={{ color: adaptive.grey800 }}
                        />
                    }
                    verticalPadding="large"
                />
                <ListRow
                    left={
                        <ListRow.Image
                            source={{
                                uri: 'https://static.toss.im/ml-product/farmer-golden-rice-plant.png',
                            }}
                            hideBorder={true}
                        />
                    }
                    contents={
                        <ListRow.Texts
                            type="1RowTypeB"
                            top="목표를 달성해봐요!"
                            topProps={{ color: adaptive.grey800 }}
                        />
                    }
                    verticalPadding="large"
                />
            </List>
            <FixedBottomCTAProvider>
                <FixedBottomCTA loading={false} bottomAccessory="로그인 없이 둘러보기">
                    페이널티 시작하기
                </FixedBottomCTA>
            </FixedBottomCTAProvider>
            <FixedBottomCTAProvider>
                <FixedBottomCTA loading={false} bottomAccessory="로그인 없이 둘러보기">
                    페이널티 시작하기
                </FixedBottomCTA>
            </FixedBottomCTAProvider>
        </>
    );
}