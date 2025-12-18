import {createRoute, Spacing} from "@granite-js/react-native";
import {Button, FixedBottomCTA, FixedBottomCTAProvider, ProgressBar, TableRow, Top, Txt} from "@toss/tds-react-native";
import {adaptive} from "@toss/tds-colors";
import {useAdaptive} from "@toss/tds-react-native/private";
import {View} from "react-native";

export const Route = createRoute('/create-goal/step4', {
    component: Page,
})

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={30}/>
            <ProgressBar progress={40} color="#3182f6" size="normal"/>
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        얼마나 자주 인증할까요?
                    </Top.TitleParagraph>
                }
                subtitle1={<Top.SubtitleBadges items={[]}/>}
                subtitle2={
                    <Top.SubtitleParagraph>
                        무리하지 않는 리듬이 오래 갑니다{'\n'}
                        실패해도 다음 리듬은 그대로 와요
                    </Top.SubtitleParagraph>
                }
            />
            <TableRow
                align="right"
                left={<TableRow.LeftText>요일</TableRow.LeftText>}
                right=""
                leftRatio={30}
            />
            <View>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    월
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    화
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    수
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    목
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    금
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    토
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    일
                </Txt>
            </View>
            <TableRow
                align="right"
                left={<TableRow.LeftText>횟수</TableRow.LeftText>}
                right=""
                leftRatio={30}
            />
            <View>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    1회
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    2회
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    3회
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    4회
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    5회
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    6회
                </Txt>
                <Txt color={adaptive.grey700} typography="st8" fontWeight="bold">
                    7회
                </Txt>
            </View>
            <FixedBottomCTAProvider>
                <FixedBottomCTA.Double
                    leftButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            disabled={false}
                            loading={false}
                            onPress={() => navigation.navigate('/create-goal/step3')}
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
                            onPress={() => navigation.navigate('/create-goal/step5')}
                        >
                            다음
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}