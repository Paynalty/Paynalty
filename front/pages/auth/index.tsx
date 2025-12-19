import {Asset, Top, List, ListRow, FixedBottomCTA, FixedBottomCTAProvider, Button} from '@toss/tds-react-native';
import {createRoute, Spacing} from '@granite-js/react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import AsyncStorage from '@react-native-async-storage/async-storage';
import {ScrollView, View} from "react-native";

export const Route = createRoute('/auth', {
    component: Page,
});

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();

    const completeOnboarding = async () => {
        try {
            await AsyncStorage.setItem('hasCompletedOnboarding', 'true');
        } catch (error) {
            console.error('Failed to save onboarding status:', error);
        }
    };

    const handleStartWithLogin = async () => {
        await completeOnboarding();
        navigation.navigate('/auth/login');
    };

    return (
        <ScrollView>
            <>
                <Spacing size={14}/>
                <Top
                    title={
                        <Top.TitleParagraph color={adaptive.grey900}>
                            못지키면, 돈이 나갑니다!
                        </Top.TitleParagraph>
                    }
                    subtitle2={
                        <Top.SubtitleParagraph>
                            목표 달성에 실패하면{'\n'}
                            정해둔 패널티가 발생해요.
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
                    <View style={{ alignItems: 'center', justifyContent: 'center' }}>
                        <Asset.Image
                            frameShape={{ width: 250, height: 250 }}
                            source={{ uri: 'https://static.toss.im/ml-product/dog-a-coin.png' }}
                            accessibilityLabel=""
                        />
                    </View>
                </>
                <List rowSeparator="none">
                    {/*List 1*/}
                    <ListRow
                        left={
                            <ListRow.Image
                                type="square"
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
                                topProps={{color: adaptive.grey800, typography: 't4', fontWeight: 'bold',}}
                            />
                        }
                        verticalPadding="small"
                    />
                    {/*List 2*/}
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
                                topProps={{color: adaptive.grey800, typography: 't4', fontWeight: 'bold',}}
                            />
                        }
                        verticalPadding="small"
                    />
                    {/*List 3*/}
                    <ListRow
                        left={
                            <ListRow.Image
                                type="square"
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
                                topProps={{color: adaptive.grey800, typography: 't4', fontWeight: 'bold',}}
                            />
                        }
                        verticalPadding="small"
                    />
                </List>
                <FixedBottomCTAProvider>
                    <FixedBottomCTA
                        loading={false}
                        onPress={handleStartWithLogin}
                    >
                        페이널티 시작하기
                    </FixedBottomCTA>
                </FixedBottomCTAProvider>
            </>
        </ScrollView>
    );
}