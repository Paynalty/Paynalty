import {createRoute, Spacing} from '@granite-js/react-native';
import {ProgressBar, Top, TextArea, FixedBottomCTA, FixedBottomCTAProvider, Button, Txt} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {useState} from "react";
import {View} from "react-native";
import {updateCreateGoalData} from '../../src/stores/createGoalStore';

export const Route = createRoute('/create-goal/step2', {
    component: Page,
});

const MAX_TITLE_LENGTH = 30;

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    const [goalTitle, setGoalTitle] = useState('');

    // 목표 제목 변경 처리
    const handleChangeTitle = (text: string) => {
        // 최대 길이 제한
        if (text.length <= MAX_TITLE_LENGTH) {
            setGoalTitle(text);
        }
    };

    // 다음 버튼 활성화 조건
    const isNextButtonEnabled = goalTitle.trim().length > 0;

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
            <View style={{paddingHorizontal: 16}}>
                <TextArea
                    label=""
                    value={goalTitle}
                    placeholder="어떤 목표든 괜찮아요!"
                    autoFocus={true}
                    onChangeText={handleChangeTitle}
                />
                {goalTitle.length >= 20 && (
                    <Txt typography="t6" color={adaptive.red500} style={{textAlign: 'right', marginTop: 0, paddingHorizontal: 28}}>
                        {`최대 ${goalTitle.length}/${MAX_TITLE_LENGTH}자 까지만 작성할 수 있어요`}
                    </Txt>
                )}
            </View>
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
                            disabled={!isNextButtonEnabled}
                            loading={false}
                            onPress={() => {
                                // 데이터 저장
                                updateCreateGoalData({ goalTitle: goalTitle.trim() });
                                // 다음 단계로 이동
                                navigation.navigate('/create-goal/step3');
                            }}
                        >
                            다음
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}