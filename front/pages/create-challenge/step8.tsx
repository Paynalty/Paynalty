import {createRoute, Spacing} from '@granite-js/react-native';
import {Asset, ProgressBar, Top, ListRow, FixedBottomCTA, FixedBottomCTAProvider, Button} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {Pressable, View} from "react-native";

export const Route = createRoute('/create-challenge/step8', {
    component: Page,
})

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={30}/>
            <ProgressBar progress={90} color="#3182f6" size="normal"/>
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        누구와 함께할까요?
                    </Top.TitleParagraph>
                }
                subtitle1={<Top.SubtitleBadges items={[]}/>}
                subtitle2={
                    <Top.SubtitleParagraph>
                        혼자보다 함께가 오래 갑니다{'\n'}
                        서로의 진행 상황을 공유해요
                    </Top.SubtitleParagraph>
                }
            />
            <Pressable onPress={() => navigation.navigate('/create-challenge/invite-friends')}>
                <ListRow
                    contents={
                        <ListRow.Texts
                            type="2RowTypeC"
                            top="추가하기"
                            topProps={{color: adaptive.grey800, fontWeight: 'bold'}}
                            bottom="5명이 참여하고 있어요"
                            bottomProps={{color: adaptive.grey500}}
                        />
                    }
                    right={
                        <View style={{ flexDirection: 'row', gap: -12, alignItems: 'center' }}>
                            <Asset.Icon
                                frameShape={{width: 42, height: 42}}
                                name="icon-plus-grey-fill-opacity"
                            />
                        </View>
                    }
                />
            </Pressable>
            <FixedBottomCTAProvider>
                <FixedBottomCTA.Double
                    leftButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            disabled={false}
                            loading={false}
                            onPress={() => navigation.navigate('/create-challenge/step7')}
                        >
                            이전
                        </Button>
                    }
                    rightButton={
                        <Button
                            type="primary"
                            style="fill"
                            display="block"
                            disabled={false}
                            loading={false}
                            onPress={() => navigation.navigate('/create-challenge/complete')}
                        >
                            다음
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}