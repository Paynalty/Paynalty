import { createRoute, Spacing } from '@granite-js/react-native';
import {
  Button,
  Carousel,
  FixedBottomCTA,
  FixedBottomCTAProvider,
  ProgressBar,
  Top,
  Txt,
} from '@toss/tds-react-native';
import { useAdaptive, Paragraph } from '@toss/tds-react-native/private';
import { Pressable, StyleSheet, View } from 'react-native';
import { useState } from 'react';
import { useCreateChallengeStore } from '../../src/stores/createChallengeStore';

export const Route = createRoute('/create-challenge/step4', {
  component: Page,
});

function Page() {
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();
  const updateData = useCreateChallengeStore((state) => state.updateData);

  const [selectionType, setSelectionType] = useState<'day' | 'count'>('day');
  const [selectedDays, setSelectedDays] = useState<string[]>([]);
  const [selectedCount, setSelectedCount] = useState<string>('');

  const days = ['월', '화', '수', '목', '금', '토', '일'];
  const counts = ['1회', '2회', '3회', '4회', '5회', '6회', '7회'];

  const cards = [
    { title: '카드 1', description: '설명 1' },
    { title: '카드 2', description: '설명 2' },
    { title: '카드 3', description: '설명 3' },
  ];

  const toggleDay = (day: string) => {
    setSelectionType('day');
    setSelectedCount('');
    if (selectedDays.includes(day)) {
      setSelectedDays(selectedDays.filter((d) => d !== day));
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
      <Spacing size={32} />
      <ProgressBar progress={40} color="#3182f6" size="normal" />
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>얼마나 자주 인증할까요?</Top.TitleParagraph>}
        subtitle1={<Top.SubtitleBadges items={[]} />}
        subtitle2={
          <Top.SubtitleParagraph>
            무리하지 않는 리듬이 오래 갑니다{'\n'}
            실패해도 다음 리듬은 그대로 와요
          </Top.SubtitleParagraph>
        }
      />
      <Spacing size={32} />

      <Paragraph.Text
        color={adaptive.grey700}
        typography="st8"
        fontWeight="bold"
        style={{ paddingHorizontal: 28, marginBottom: 8 }}
      >
        요일
      </Paragraph.Text>
      <Carousel itemWidth={76} padding={16}>
        {days.map((day) => {
          const isSelected = selectedDays.includes(day);
          return (
            <Carousel.Item key={day}>
              <Pressable
                onPress={() => toggleDay(day)}
                style={[styles.dayButton, isSelected ? styles.selectedButton : styles.unselectedButton]}
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
      <Spacing size={32} />
      <Paragraph.Text
        color={adaptive.grey700}
        typography="st8"
        fontWeight="bold"
        style={{ paddingHorizontal: 28, marginBottom: 8 }}
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
                style={[styles.countButton, isSelected ? styles.selectedButton : styles.unselectedButton]}
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
              onPress={() => navigation.navigate('/create-challenge/step3')}
            >
              이전
            </Button>
          }
          rightButton={
            <Button
              type="primary"
              style="fill"
              display="block"
              disabled={
                !((selectionType === 'day' && selectedDays.length > 0) || (selectionType === 'count' && selectedCount))
              }
              loading={false}
              onPress={() => {
                const dayMapping: { [key: string]: string } = {
                  월: 'MON',
                  화: 'TUE',
                  수: 'WED',
                  목: 'THU',
                  금: 'FRI',
                  토: 'SAT',
                  일: 'SUN',
                };
                const periodValue = selectionType === 'count' ? selectedCount : selectedDays.join(', ');
                const mappedDays =
                  selectionType === 'day' ? selectedDays.map((d) => dayMapping[d] || 'MON') : undefined;

                updateData({
                  period: periodValue,
                  startDate: selectionType,
                  dayOfWeek: mappedDays,
                  frequency: selectionType === 'day' ? mappedDays?.length : Number(selectedCount.replace('회', '')),
                });
                navigation.navigate('/create-challenge/step5');
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
    backgroundColor: '#3182f6',
  },
  unselectedButton: {
    backgroundColor: '#f9fafb',
  },
});
