import { Button, FixedBottomCTA, FixedBottomCTAProvider, ProgressBar, Top, Asset, Txt } from '@toss/tds-react-native';
import { createRoute, Spacing } from '@granite-js/react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { useState } from 'react';
import { View, Modal, ScrollView, Pressable, StyleSheet } from 'react-native';
import { useCreateGoalStore } from '../../src/stores/createGoalStore';

export const Route = createRoute('/create-goal/step5', {
  component: Page,
});

function Page() {
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();
  const updateData = useCreateGoalStore((s) => s.updateData);

  const [startTime, setStartTime] = useState('00:00');
  const [endTime, setEndTime] = useState('24:00');
  const [showStartPicker, setShowStartPicker] = useState(false);
  const [showEndPicker, setShowEndPicker] = useState(false);

  const hours = Array.from({ length: 25 }, (_, i) => (i === 24 ? '24:00' : `${String(i).padStart(2, '0')}:00`));

  const handleSelectStartTime = (time: string) => {
    setStartTime(time);
    setShowStartPicker(false);
  };

  const handleSelectEndTime = (time: string) => {
    setEndTime(time);
    setShowEndPicker(false);
  };

  const handleNext = () => {
    // endTime이 24:00이면 23:59:59로 변환
    const normalizedEndTime = endTime === '24:00' ? '23:59:59' : endTime;

    updateData({
      verifyStartAt: startTime,
      verifyEndAt: normalizedEndTime,
    });
    navigation.navigate('/create-goal/step6');
  };

  return (
    <>
      <Spacing size={30} />
      <ProgressBar progress={45} color="#3182f6" size="normal" />
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>언제 인증할까요?</Top.TitleParagraph>}
        subtitle1={<Top.SubtitleBadges items={[]} />}
        subtitle2={
          <Top.SubtitleParagraph>
            이 시간 안에 인증하면 자동으로 성공 처리돼요{'\n'}
            따로 판단하지 않아도 돼요
          </Top.SubtitleParagraph>
        }
      />

      <View style={{ paddingHorizontal: 16, gap: 16 }}>
        <Spacing size={20} />

        {/* 시작 시간 */}
        <Pressable onPress={() => setShowStartPicker(true)}>
          <View style={[styles.timeCard, { backgroundColor: adaptive.grey50 }]}>
            <View style={styles.timeContent}>
              <Txt typography="t6" color={adaptive.grey700} fontWeight="bold">
                시작 시간
              </Txt>
              <Txt typography="t3" color={adaptive.grey900} fontWeight="bold">
                {startTime}
              </Txt>
            </View>
            <Asset.Icon frameShape={Asset.frameShape.CleanW24} name="icon-arrow-right-mono" color={adaptive.grey400} />
          </View>
        </Pressable>

        {/* 마감 시간 */}
        <Pressable onPress={() => setShowEndPicker(true)}>
          <View style={[styles.timeCard, { backgroundColor: adaptive.grey50 }]}>
            <View style={styles.timeContent}>
              <Txt typography="t6" color={adaptive.grey700} fontWeight="bold">
                마감 시간
              </Txt>
              <Txt typography="t3" color={adaptive.grey900} fontWeight="bold">
                {endTime}
              </Txt>
            </View>
            <Asset.Icon frameShape={Asset.frameShape.CleanW24} name="icon-arrow-right-mono" color={adaptive.grey400} />
          </View>
        </Pressable>
      </View>

      {/* 시작 시간 선택 모달 */}
      <Modal visible={showStartPicker} transparent animationType="slide">
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <View
            style={{ backgroundColor: 'white', borderTopLeftRadius: 20, borderTopRightRadius: 20, maxHeight: '50%' }}
          >
            <View style={{ padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey200 }}>
              <Txt typography="t4" fontWeight="bold">
                시작 시간 선택
              </Txt>
            </View>
            <ScrollView>
              {hours.map((time) => (
                <Pressable
                  key={time}
                  onPress={() => handleSelectStartTime(time)}
                  style={{ padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey100 }}
                >
                  <Txt typography="t5" color={time === startTime ? adaptive.blue500 : adaptive.grey900}>
                    {time}
                  </Txt>
                </Pressable>
              ))}
            </ScrollView>
            <Pressable onPress={() => setShowStartPicker(false)} style={{ padding: 16 }}>
              <Txt typography="t5" color={adaptive.grey600} style={{ textAlign: 'center' }}>
                닫기
              </Txt>
            </Pressable>
          </View>
        </View>
      </Modal>

      {/* 마감 시간 선택 모달 */}
      <Modal visible={showEndPicker} transparent animationType="slide">
        <View style={{ flex: 1, justifyContent: 'flex-end', backgroundColor: 'rgba(0,0,0,0.5)' }}>
          <View
            style={{ backgroundColor: 'white', borderTopLeftRadius: 20, borderTopRightRadius: 20, maxHeight: '50%' }}
          >
            <View style={{ padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey200 }}>
              <Txt typography="t4" fontWeight="bold">
                마감 시간 선택
              </Txt>
            </View>
            <ScrollView>
              {hours.map((time) => (
                <Pressable
                  key={time}
                  onPress={() => handleSelectEndTime(time)}
                  style={{ padding: 16, borderBottomWidth: 1, borderBottomColor: adaptive.grey100 }}
                >
                  <Txt typography="t5" color={time === endTime ? adaptive.blue500 : adaptive.grey900}>
                    {time}
                  </Txt>
                </Pressable>
              ))}
            </ScrollView>
            <Pressable onPress={() => setShowEndPicker(false)} style={{ padding: 16 }}>
              <Txt typography="t5" color={adaptive.grey600} style={{ textAlign: 'center' }}>
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
              onPress={() => navigation.navigate('/create-goal/step4')}
            >
              이전
            </Button>
          }
          rightButton={
            <Button type="primary" style="fill" display="block" disabled={false} loading={false} onPress={handleNext}>
              다음
            </Button>
          }
        />
      </FixedBottomCTAProvider>
    </>
  );
}

const styles = StyleSheet.create({
  timeCard: {
    flexDirection: 'row',
    alignItems: 'center',
    justifyContent: 'space-between',
    paddingHorizontal: 16,
    paddingVertical: 16,
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#e0e0e0',
  },
  timeContent: {
    flexDirection: 'column',
    gap: 4,
  },
});
