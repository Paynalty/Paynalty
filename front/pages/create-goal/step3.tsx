import {createRoute, Spacing} from '@granite-js/react-native';
import {Button, FixedBottomCTA, FixedBottomCTAProvider, ProgressBar, Top, TextField, Asset, Txt} from "@toss/tds-react-native";
import {useAdaptive} from "@toss/tds-react-native/private";
import {useState} from "react";
import {View, Modal, ScrollView, Pressable} from "react-native";
import {updateCreateGoalData} from '../../src/stores/createGoalStore';

export const Route = createRoute('/create-goal/step3', {
    component: Page,
})

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();

    const currentDate = new Date();
    const [year, setYear] = useState(currentDate.getFullYear());
    const [month, setMonth] = useState(currentDate.getMonth() + 1);
    const [day, setDay] = useState(currentDate.getDate());

    const [showYearPicker, setShowYearPicker] = useState(false);
    const [showMonthPicker, setShowMonthPicker] = useState(false);
    const [showDayPicker, setShowDayPicker] = useState(false);

    // 년도 목록 (현재 년도부터 +5년)
    const years = Array.from({length: 5}, (_, i) => currentDate.getFullYear() + i);

    // 월 목록
    const months = Array.from({length: 12}, (_, i) => i + 1);

    // 일 목록 (선택된 년/월에 따라 마지막 날 계산)
    const getDaysInMonth = (year: number, month: number) => {
        return new Date(year, month, 0).getDate();
    };
    const days = Array.from({length: getDaysInMonth(year, month)}, (_, i) => i + 1);

    // 날짜 포맷팅
    const formattedDate = `${year}년 ${month}월 ${day}일`;

    return (
        <>
            <Spacing size={30}/>
            <ProgressBar progress={35} color="#3182f6" size="normal"/>
            <Top
                title={
                    <Top.TitleParagraph color={adaptive.grey900}>
                        언제까지 도전할까요?
                    </Top.TitleParagraph>
                }
                subtitle1={<Top.SubtitleBadges items={[]}/>}
                subtitle2={
                    <Top.SubtitleParagraph>
                        이 날짜까지 결과가 기록돼요{'\n'}
                        중간에 실패해도 계속 진행돼요
                    </Top.SubtitleParagraph>
                }
            />

            <View style={{paddingHorizontal: 16, gap: 12}}>
                <Spacing size={20}/>

                {/* 년/월/일 선택 */}
                <View style={{flexDirection: 'row', gap: 8}}>
                    {/* 년 선택 */}
                    <View style={{flex: 1}}>
                        <Pressable onPress={() => setShowYearPicker(true)}>
                            <TextField
                                variant="box"
                                label=""
                                value={`${year}`}
                                editable={false}
                                right={
                                    <Asset.Icon
                                        frameShape={Asset.frameShape.CleanW24}
                                        name="icon-arrow-down-mono"
                                        color={adaptive.grey400}
                                    />
                                }
                            />
                        </Pressable>
                    </View>

                    {/* 월 선택 */}
                    <View style={{flex: 1}}>
                        <Pressable onPress={() => setShowMonthPicker(true)}>
                            <TextField
                                variant="box"
                                label=""
                                value={`${month}`}
                                editable={false}
                                right={
                                    <Asset.Icon
                                        frameShape={Asset.frameShape.CleanW24}
                                        name="icon-arrow-down-mono"
                                        color={adaptive.grey400}
                                    />
                                }
                            />
                        </Pressable>
                    </View>

                    {/* 일 선택 */}
                    <View style={{flex: 1}}>
                        <Pressable onPress={() => setShowDayPicker(true)}>
                            <TextField
                                variant="box"
                                label=""
                                value={`${day}`}
                                editable={false}
                                right={
                                    <Asset.Icon
                                        frameShape={Asset.frameShape.CleanW24}
                                        name="icon-arrow-down-mono"
                                        color={adaptive.grey400}
                                    />
                                }
                            />
                        </Pressable>
                    </View>
                </View>
            </View>

            {/* 년도 선택 모달 */}
            <Modal visible={showYearPicker} transparent animationType="slide">
                <View style={{flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(0,0,0,0.5)'}}>
                    <View style={{backgroundColor: 'white', borderTopLeftRadius: 20, borderTopRightRadius: 20, maxHeight: '50%'}}>
                        <View style={{padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey200}}>
                            <Txt typography="t4" fontWeight="bold">년도 선택</Txt>
                        </View>
                        <ScrollView>
                            {years.map((y) => (
                                <Pressable
                                    key={y}
                                    onPress={() => {
                                        setYear(y);
                                        setShowYearPicker(false);
                                    }}
                                    style={{padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey100}}
                                >
                                    <Txt typography="t5" color={y === year ? adaptive.blue500 : adaptive.grey900}>
                                        {y}년
                                    </Txt>
                                </Pressable>
                            ))}
                        </ScrollView>
                        <Pressable onPress={() => setShowYearPicker(false)} style={{padding: 16}}>
                            <Txt typography="t5" color={adaptive.grey600} style={{textAlign: 'center'}}>
                                닫기
                            </Txt>
                        </Pressable>
                    </View>
                </View>
            </Modal>

            {/* 월 선택 모달 */}
            <Modal visible={showMonthPicker} transparent animationType="slide">
                <View style={{flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(0,0,0,0.5)'}}>
                    <View style={{backgroundColor: 'white', borderTopLeftRadius: 20, borderTopRightRadius: 20, maxHeight: '50%'}}>
                        <View style={{padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey200}}>
                            <Txt typography="t4" fontWeight="bold">월 선택</Txt>
                        </View>
                        <ScrollView>
                            {months.map((m) => (
                                <Pressable
                                    key={m}
                                    onPress={() => {
                                        setMonth(m);
                                        setShowMonthPicker(false);
                                    }}
                                    style={{padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey100}}
                                >
                                    <Txt typography="t5" color={m === month ? adaptive.blue500 : adaptive.grey900}>
                                        {m}월
                                    </Txt>
                                </Pressable>
                            ))}
                        </ScrollView>
                        <Pressable onPress={() => setShowMonthPicker(false)} style={{padding: 16}}>
                            <Txt typography="t5" color={adaptive.grey600} style={{textAlign: 'center'}}>
                                닫기
                            </Txt>
                        </Pressable>
                    </View>
                </View>
            </Modal>

            {/* 일 선택 모달 */}
            <Modal visible={showDayPicker} transparent animationType="slide">
                <View style={{flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(0,0,0,0.5)'}}>
                    <View style={{backgroundColor: 'white', borderTopLeftRadius: 20, borderTopRightRadius: 20, maxHeight: '50%'}}>
                        <View style={{padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey200}}>
                            <Txt typography="t4" fontWeight="bold">일 선택</Txt>
                        </View>
                        <ScrollView>
                            {days.map((d) => (
                                <Pressable
                                    key={d}
                                    onPress={() => {
                                        setDay(d);
                                        setShowDayPicker(false);
                                    }}
                                    style={{padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey100}}
                                >
                                    <Txt typography="t5" color={d === day ? adaptive.blue500 : adaptive.grey900}>
                                        {d}일
                                    </Txt>
                                </Pressable>
                            ))}
                        </ScrollView>
                        <Pressable onPress={() => setShowDayPicker(false)} style={{padding: 16}}>
                            <Txt typography="t5" color={adaptive.grey600} style={{textAlign: 'center'}}>
                                닫기
                            </Txt>
                        </Pressable>
                    </View>
                </View>
            </Modal>

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
                            onPress={() => {
                                // 데이터 저장
                                updateCreateGoalData({
                                    deadline: formattedDate,
                                });
                                // 다음 단계로 이동
                                navigation.navigate('/create-goal/step4');
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