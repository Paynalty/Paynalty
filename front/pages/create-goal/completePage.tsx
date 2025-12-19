import {createRoute, Spacing} from '@granite-js/react-native';
import {Asset, Txt, FixedBottomCTA, FixedBottomCTAProvider, Button, List, ListRow} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';

export const Route = createRoute('/create-goal/completePage', {
    component: Page,
})

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={10}/>
            <>
                <Asset.Image
                    frameShape={{width: 100}}
                    source={{uri: 'https://static.toss.im/lotties/check-spot-apng.png'}}
                />
            </>
            <Spacing size={24}/>
            <Txt color={adaptive.grey800} typography="t2" fontWeight="bold">
                목표를 만들었어요
            </Txt>
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
                            topProps={{ color: adaptive.grey600 }}
                            bottom="런닝 3KM"
                            bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
                        />
                    }
                    verticalPadding="large"
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
                            topProps={{ color: adaptive.grey600 }}
                            bottom="2025년 8월 25일"
                            bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
                        />
                    }
                    verticalPadding="large"
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
                            topProps={{ color: adaptive.grey600 }}
                            bottom="주 3회"
                            bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
                        />
                    }
                    verticalPadding="large"
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
                            topProps={{ color: adaptive.grey600 }}
                            bottom="사진"
                            bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
                        />
                    }
                    verticalPadding="large"
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
                            topProps={{ color: adaptive.grey600 }}
                            bottom="10,000원"
                            bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
                        />
                    }
                    verticalPadding="large"
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