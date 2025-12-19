import {createRoute, Spacing} from '@granite-js/react-native';
import {ProgressBar, Top, FixedBottomCTA, FixedBottomCTAProvider, Button} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';

export const Route = createRoute('/create-goal/step7', {
    component: Page,
})

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={30}/>
            <ProgressBar progress={80} color="#3182f6" size="normal"/>
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        인증을 놓치면 얼마를 낼까요?
                    </Top.TitleParagraph>
                }
                subtitle1={<Top.SubtitleBadges items={[]}/>}
                subtitle2={
                    <Top.SubtitleParagraph>
                        실패는 종료가 아니라 비용이에요{'\n'}
                        다음 도전은 그대로 이어집니다
                    </Top.SubtitleParagraph>
                }
            />
            <FixedBottomCTAProvider>
                <FixedBottomCTA.Double
                    leftButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            disabled={false}
                            loading={false}
                        >
                            5,000원
                        </Button>
                    }
                    rightButton={
                        <Button
                            type="primary"
                            style="fill"
                            display="block"
                            disabled={false}
                            loading={false}
                        >
                            10,000원
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
            <FixedBottomCTAProvider>
                <FixedBottomCTA.Double
                    leftButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            disabled={false}
                            loading={false}
                        >
                            20,000원
                        </Button>
                    }
                    rightButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            disabled={false}
                            loading={false}
                        >
                            직접 입력하기
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
            <FixedBottomCTAProvider>
                <FixedBottomCTA.Double
                    leftButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            disabled={false}
                            loading={false}
                            onPress={() => navigation.navigate('/create-goal/step6')}
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
                            onPress={() => navigation.navigate('/create-goal/step8')}
                        >
                            다음
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}