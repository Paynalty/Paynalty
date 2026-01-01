import {Asset, Txt, Top, FixedBottomCTA, FixedBottomCTAProvider, StepperRow,} from '@toss/tds-react-native';
import {createRoute, Spacing, useNavigation} from '@granite-js/react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {View} from "react-native";

export const Route = createRoute('/create-challenge', {
    component: Page,
});

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={12}/>
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        목표를 이루는 여정,{'\n'}
                        페이널티가 도와줄게요
                    </Top.TitleParagraph>
                }
            />
            <View style={{ alignItems: 'center' }}>
                <Asset.Image
                    frameShape={{ width: 130, height: 130 }}
                    source={{ uri: 'https://i.ibb.co/gYFDpGL/AIDrawing-251220-8ea29a0a-8911-4094-b2c5-6be96d47c2af-0-Miri-Canvas.png' }}
                />
            </View>
            <Spacing size={32}/>
            <StepperRow
                left={<StepperRow.NumberIcon number={1} />}
                center={<StepperRow.Texts type="A" title="목표 설정하기" description="하고 싶은 일을 정해요" />}
            />
            <StepperRow
                left={<StepperRow.NumberIcon number={2} />}
                center={<StepperRow.Texts type="A" title="인증 리듬 정하기" description="언제 얼마나 자주 인증할지 정해요" />}
            />
            <StepperRow
                left={<StepperRow.NumberIcon number={3} />}
                center={<StepperRow.Texts type="A" title="인증 방식 고르기" description="어떻게 인증할지 정해요" />}
            />
            <StepperRow
                left={<StepperRow.NumberIcon number={4} />}
                center={<StepperRow.Texts type="A" title="패널티 정하기" description="패널티를 감당할 수 있는 금액을 정해요" />}
            />
            <StepperRow
                left={<StepperRow.NumberIcon number={5} />}
                center={<StepperRow.Texts type="C" title="함께할 친구 초대하기" description="혼자보다 함께가 오래갑니다" />}
                hideLine
            />
            <FixedBottomCTAProvider>
                <FixedBottomCTA loading={false} onPress={() =>
                    navigation.navigate('/create-challenge/step2')}>목표 만들기</FixedBottomCTA>
            </FixedBottomCTAProvider>
        </>
    );
}