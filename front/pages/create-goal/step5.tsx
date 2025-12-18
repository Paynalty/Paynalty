import {Button, FixedBottomCTA, FixedBottomCTAProvider, ProgressBar, TableRow, Top} from "@toss/tds-react-native";
import {createRoute, Spacing} from "@granite-js/react-native";
import {adaptive} from "@toss/tds-colors";
import {useAdaptive} from "@toss/tds-react-native/private";

export const Route=createRoute('/create-goal/step5', {
    component:Page,
})

function Page() {
    const adaptive = useAdaptive();
    const navigation= Route.useNavigation();
    return (
        <>
            <Spacing size={30}></Spacing>
            <ProgressBar progress={45} color="#3182f6" size="normal" />
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        언제 인증할까요?
                    </Top.TitleParagraph>
                }
                subtitle1={<Top.SubtitleBadges items={[]} />}
                subtitle2={
                    <Top.SubtitleParagraph>
                        이 시간 안에 인증하면 자동으로 성공 처리돼요{'\n'}
                        따로 판단하지 않아도 돼요
                    </Top.SubtitleParagraph>
                }
            />
            <TableRow
                align="right"
                left={<TableRow.LeftText fontWeight="bold">시작 시간</TableRow.LeftText>}
                right={
                    <TableRow.RightText color={adaptive.grey700} fontWeight="bold">
                        00:00{' '}
                    </TableRow.RightText>
                }
                leftRatio={30}
            />
            <TableRow
                align="right"
                left={<TableRow.LeftText fontWeight="bold">마감 시간</TableRow.LeftText>}
                right={
                    <TableRow.RightText color={adaptive.grey700} fontWeight="bold">
                        23:00{' '}
                    </TableRow.RightText>
                }
                leftRatio={30}
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
                            onPress={() => navigation.navigate("/create-goal/step4")}
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
                            onPress={() => navigation.navigate("/create-goal/step6")}
                        >
                            다음
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    )
}