import {createRoute, Spacing} from '@granite-js/react-native';
import {Asset, FixedBottomCTA, FixedBottomCTAProvider, Button, List, ListRow, Top} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';

export const Route = createRoute('/create-goal/complete', {
    component: Page,
})

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={30}/>
            <Top
                upper={
                    <Top.UpperAssetContent
                        content={
                            <Asset.Lottie
                                frameShape={Asset.frameShape.SquareLarge}
                                scale={1}
                                src="https://static.toss.im/lotties-common/check-blue-spot.json"
                            />
                        }
                    />
                }
                title={<Top.TitleParagraph size={28}>목표를 만들었어요</Top.TitleParagraph>}
            />
            <List rowSeparator="none">
                <ListRow
                    left={
                        <ListRow.Image
                            type="circle"
                            source={{
                                uri: 'https://static.toss.im/ml-product/square-salt-topped-saltbread.png',
                            }}
                            hideBorder={true}
                        />
                    }
                    contents={
                        <ListRow.Texts
                            type="2RowTypeD"
                            top="목표"
                            topProps={{color: adaptive.grey600}}
                            bottom="런닝 3KM"
                            bottomProps={{color: adaptive.grey800, fontWeight: 'bold'}}
                        />
                    }
                    verticalPadding="small"
                />
                <ListRow
                    left={
                        <ListRow.Image
                            type="circle"
                            source={{
                                uri: 'https://static.toss.im/ml-product/yellow-slippers.png',
                            }}
                            hideBorder={true}
                        />
                    }
                    contents={
                        <ListRow.Texts
                            type="2RowTypeD"
                            top="마감일"
                            topProps={{color: adaptive.grey600}}
                            bottom="2025년 8월 25일"
                            bottomProps={{color: adaptive.grey800, fontWeight: 'bold'}}
                        />
                    }
                    verticalPadding="small"
                />
                <ListRow
                    left={
                        <ListRow.Image
                            type="circle"
                            source={{
                                uri: 'https://static.toss.im/ml-product/workgloves-constructiongloves.png',
                            }}
                            hideBorder={true}
                        />
                    }
                    contents={
                        <ListRow.Texts
                            type="2RowTypeD"
                            top="인증 주기"
                            topProps={{color: adaptive.grey600}}
                            bottom="주 3회"
                            bottomProps={{color: adaptive.grey800, fontWeight: 'bold'}}
                        />
                    }
                    verticalPadding="small"
                />
                <ListRow
                    left={
                        <ListRow.Image
                            type="circle"
                            source={{
                                uri: 'https://static.toss.im/ml-product/rubber-duck.png',
                            }}
                            hideBorder={true}
                        />
                    }
                    contents={
                        <ListRow.Texts
                            type="2RowTypeD"
                            top="인증 방법"
                            topProps={{color: adaptive.grey600}}
                            bottom="사진"
                            bottomProps={{color: adaptive.grey800, fontWeight: 'bold'}}
                        />
                    }
                    verticalPadding="small"
                />
                <ListRow
                    left={
                        <ListRow.Image
                            type="circle"
                            source={{
                                uri: 'https://static.toss.im/ml-product/squirrel-sitting-left.png',
                            }}
                            hideBorder={true}
                        />
                    }
                    contents={
                        <ListRow.Texts
                            type="2RowTypeD"
                            top="벌금"
                            topProps={{color: adaptive.grey600}}
                            bottom="10,000원"
                            bottomProps={{color: adaptive.grey800, fontWeight: 'bold'}}
                        />
                    }
                    verticalPadding="small"
                />
            </List>
            <FixedBottomCTAProvider>
                <FixedBottomCTA.Double
                    leftButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            disabled={false}
                            loading={false}
                            onPress={() => navigation.navigate('/')} // TODO : 해당 디테일 페이지로 가도록
                        >
                            다음주부터 시작하기
                        </Button>
                    }
                    rightButton={
                        <Button
                            type="primary"
                            style="fill"
                            display="block"
                            disabled={false}
                            loading={false}
                            onPress={() => navigation.navigate('/')}// TODO : 해당 디테일 페이지로 가도록
                        >
                            내일부터 시작하기
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}