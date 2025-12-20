import {createRoute, Spacing} from '@granite-js/react-native';
import {ProgressBar, Top, TextArea, FixedBottomCTA, FixedBottomCTAProvider, Button} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';

export const Route = createRoute('/create-goal/step2', {
    component: Page,
});

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={30}/>
            <ProgressBar progress={20} color="#3182f6" size="normal"/>
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        어떤 목표를 달성하고 싶나요?
                    </Top.TitleParagraph>
                }
                subtitle1={<Top.SubtitleBadges items={[]}/>}
                subtitle2={
                    <Top.SubtitleParagraph>하고 싶은 일을 정해요</Top.SubtitleParagraph>
                }
                lowerGap={0}
            />
            <TextArea
                label=""
                value=""
                placeholder="어떤 목표든 괜찮아요!"
                error={false}
                autoFocus={false}
                height={100}
            />
            <Spacing size={24}/>

            {/* TODO : 템플릿 제공*/}
            <FixedBottomCTAProvider>
                <FixedBottomCTA.Double
                    leftButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            disabled={false}
                            loading={false}
                            onPress={() => navigation.navigate('/create-goal')}
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
                            onPress={() => navigation.navigate('/create-goal/step3')}
                        >
                            다음
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}