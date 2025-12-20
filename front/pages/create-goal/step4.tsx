import {createRoute, Spacing} from "@granite-js/react-native";
import {
    Button, Carousel, FixedBottomCTA, FixedBottomCTAProvider, ProgressBar, Top
} from "@toss/tds-react-native";
import {useAdaptive, Paragraph} from "@toss/tds-react-native/private";
import {Pressable, StyleSheet} from "react-native";
import {useState} from "react";
import {updateCreateGoalData} from '../../src/stores/createGoalStore';

export const Route = createRoute('/create-goal/step4', {
    component: Page,
})

function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();

    const [selectionType, setSelectionType] = useState<'day' | 'count'>('day');
    const [selectedDays, setSelectedDays] = useState<string[]>([]);
    const [selectedCount, setSelectedCount] = useState<string>('');

    const days = ['월', '화', '수', '목', '금', '토', '일'];
    const counts = ['1회', '2회', '3회', '4회', '5회', '6회', '7회'];

    const toggleDay = (day: string) => {
        setSelectionType('day');
        setSelectedCount('');
        if (selectedDays.includes(day)) {
            setSelectedDays(selectedDays.filter(d => d !== day));
        } else {
            setSelectedDays([...selectedDays, day]);
        }
    };

    const selectCount = (count: string) => {
        setSelectionType('count');
        setSelectedDays([]);
        setSelectedCount(count);
    };

    return (
        <>
            <Spacing size={32}/>
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
            <Spacing size={32}/>
            <Paragraph.Text
                color={adaptive.grey700}
                typography="st8"
                fontWeight="bold"
                style={{paddingHorizontal: 28, marginBottom: 8}}
            >
            요일
            </Paragraph.Text>
            <Carousel itemWidth={76}>
                {days.map((day) => {
                    const isSelected = selectedDays.includes(day);
                    return (
                        <Carousel.Item key={day}>
                            <Pressable
                                onPress={() => toggleDay(day)}
                                style={[
                                    styles.dayButton,
                                    isSelected ? styles.selectedButton : styles.unselectedButton
                                ]}
                            >
                                <Paragraph.Text
                                    color={isSelected ? adaptive.background : adaptive.grey700}
                                    typography="st8"
                                    fontWeight="bold"
                                >
                                    {day}
                                </Paragraph.Text>
                            </Pressable>
                        </Carousel.Item>
                    );
                })}
            </Carousel>
            <Spacing size={32}/>
            <Paragraph.Text
                color={adaptive.grey700}
                typography="st8"
                fontWeight="bold"
                style={{paddingHorizontal: 28, marginBottom: 8}}
            >
            횟수
            </Paragraph.Text>
            <Carousel itemWidth={76}>
                {counts.map((count) => {
                    const isSelected = selectedCount === count;
                    return (
                        <Carousel.Item key={count}>
                            <Pressable
                                onPress={() => selectCount(count)}
                                style={[
                                    styles.countButton,
                                    isSelected ? styles.selectedButton : styles.unselectedButton
                                ]}
                            >
                                <Paragraph.Text
                                    color={isSelected ? adaptive.background : adaptive.grey700}
                                    typography="st8"
                                    fontWeight="bold"
                                >
                                    {count}
                                </Paragraph.Text>
                            </Pressable>
                        </Carousel.Item>
                    );
                })}
            </Carousel>
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
                            disabled={!((selectionType === 'day' && selectedDays.length > 0) || (selectionType === 'count' && selectedCount))}
                            loading={false}
                            onPress={() => {
                                const periodValue = selectionType === 'count' ? selectedCount : selectedDays.join(', ');
                                updateCreateGoalData({
                                    period: periodValue,
                                    selectionType: selectionType,
                                });
                                navigation.navigate('/create-goal/step5');
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

const styles = StyleSheet.create({
    dayButton: {
        width: 68,
        height: 73,
        justifyContent: 'center',
        alignItems: 'center',
        borderRadius: 12,
    },
    countButton: {
        width: 68,
        height: 73,
        justifyContent: 'center',
        alignItems: 'center',
        borderRadius: 12,
    },
    selectedButton: {
        backgroundColor: '#4b4ddc',
    },
    unselectedButton: {
        backgroundColor: '#f9fafb',
    },
});