import {
    Asset, ProgressBar, Top, Button, ListRow, TextField, FixedBottomCTA, FixedBottomCTAProvider
} from '@toss/tds-react-native';
import {createRoute, Spacing} from '@granite-js/react-native';
import {useAdaptive} from '@toss/tds-react-native/private';

export const Route = createRoute('/create-goal/step6', {
    component: Page,
})

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={30}/>
            <ProgressBar progress={60} color="#3182f6" size="normal"/>
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        어떻게 인증할까요?
                    </Top.TitleParagraph>
                }
                subtitle1={<Top.SubtitleBadges items={[]}/>}
                subtitle2={
                    <Top.SubtitleParagraph>
                        가장 부담 없는 방식이면 충분해요 증명보다 지속이 중요해요
                    </Top.SubtitleParagraph>
                }
            />
            <Button size="large">사진</Button>
            <Button size="large" style="weak" type="dark">
                {' '}
                텍스트
            </Button>
            <Button size="large" style="weak" type="dark">
                체크
            </Button>
            <Spacing size={40}/>
            <ListRow
                left={
                    <ListRow.ImageContainer type="square" style={{}} hideBorder={true}/>
                }
                contents={
                    <ListRow.Texts
                        type="1RowTypeA"
                        top="가이드라인을 정하시겠어요 ?"
                        topProps={{color: adaptive.grey700}}
                    />
                }
                verticalPadding="large"
            />
            <TextField
                variant="box"
                label=""
                labelOption="sustain"
                value=""
                placeholder="사진/텍스트"
                editable={false}
                right={
                    <>
                        <Asset.Icon
                            frameShape={Asset.frameShape.CleanW24}
                            name="icon-arrow-down-mono"
                            color={adaptive.grey400}
                        />
                    </>
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
                            onPress={() => navigation.navigate('/create-goal/step5')}
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
                            onPress={() => navigation.navigate('/create-goal/step7')}
                        >
                            다음
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}