import {createRoute, Spacing} from '@granite-js/react-native';
import {Button, FixedBottomCTA, FixedBottomCTAProvider, ProgressBar, Top} from "@toss/tds-react-native";
import {adaptive} from "@toss/tds-colors";
import {useAdaptive} from "@toss/tds-react-native/private";

export const Route = createRoute('/create-goal/step3', {
  component : Page,
})

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={30}/>
            <ProgressBar progress={35} color="#3182f6" size="normal" />
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        언제까지 도전할까요?
                    </Top.TitleParagraph>
                }
                subtitle1={<Top.SubtitleBadges items={[]} />}
                subtitle2={
                    <Top.SubtitleParagraph>
                        이 날짜까지 결과가 기록돼요{'\n'}
                        중간에 실패해도 계속 진행돼요
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
                            onPress={() => navigation.navigate('/create-goal/step2')}
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
                            onPress={() => navigation.navigate('/create-goal/step4')}
                        >
                            다음
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}