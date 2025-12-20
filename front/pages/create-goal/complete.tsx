import {createRoute, Spacing} from '@granite-js/react-native';
import {Asset, FixedBottomCTA, FixedBottomCTAProvider, Button, List, ListRow, Top} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {useState} from 'react';
import {getCreateGoalData, resetCreateGoalData} from '../../src/stores/createGoalStore';

export const Route = createRoute('/create-goal/complete', {
    component: Page,
})

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    const [loading, setLoading] = useState<StartType | null>(null);

    type StartType = 'nextWeek' | 'tomorrow';

    // 저장된 데이터 가져오기
    const goalData = getCreateGoalData();


    // API로 목표 생성 요청
    const handleCreateGoal = async (startDate: 'tomorrow' | 'nextWeek') => {
        try {
            setLoading(startDate);

            // API 요청
            const response = await fetch('https://your-api.com/api/goals', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    goalTitle: goalData.goalTitle,
                    verificationMethod: goalData.verificationMethod,
                    penaltyAmount: goalData.penaltyAmount === 'custom'
                        ? Number(goalData.customAmount)
                        : goalData.penaltyAmount,
                    startDate: startDate,
                    // ... 다른 필드들
                }),
            });

            const result = await response.json();

            if (response.ok) {
                // 성공 시 데이터 초기화
                resetCreateGoalData();

                // 메인 페이지로 이동
                navigation.navigate('/');
            } else {
                // 에러 처리
                console.error('목표 생성 실패:', result);
                alert('목표 생성에 실패했습니다.');
            }
        } catch (error) {
            console.error('API 요청 에러:', error);
            alert('네트워크 오류가 발생했습니다.');
        } finally {
            setLoading(null);
        }
    };
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
                            bottom={goalData.goalTitle}
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
                            bottom={goalData.period}
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
                            bottom={goalData.verificationMethod}
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
                            bottom={
                                goalData.penaltyAmount === 'custom'
                                    ? `${Number(goalData.customAmount).toLocaleString()}원`
                                    : `${Number(goalData.penaltyAmount).toLocaleString()}원`
                            }
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
                            disabled={loading !== null}
                            loading={loading === 'nextWeek'}
                            onPress={() => handleCreateGoal('nextWeek')}
                        >
                            다음주부터 시작하기
                        </Button>
                    }
                    rightButton={
                        <Button
                            type="primary"
                            style="fill"
                            display="block"
                            disabled={loading !== null}
                            loading={loading === 'tomorrow'}
                            onPress={() => handleCreateGoal('tomorrow')}
                        >
                            내일부터 시작하기
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}