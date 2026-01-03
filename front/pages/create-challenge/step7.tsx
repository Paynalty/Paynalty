import { createRoute, Spacing } from '@granite-js/react-native';
import {
  ProgressBar,
  Top,
  FixedBottomCTA,
  FixedBottomCTAProvider,
  Button,
  TextField,
  Txt,
} from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { View } from 'react-native';
import { useState } from 'react';
import { useCreateChallengeStore } from '../../src/stores/createChallengeStore';

export const Route = createRoute('/create-challenge/step7', {
  component: Page,
});

const PENALTY_OPTIONS = [
  { value: 1000, label: '1,000원' },
  { value: 5000, label: '5,000원' },
  { value: 10000, label: '10,000원' },
  { value: 'custom', label: '직접 입력하기' },
] as const;

export default function Page() {
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();
  const updateData = useCreateChallengeStore((s) => s.updateData);
  const [selectAmount, setSelectAmount] = useState<number | string>(10000);
  const [customAmount, setCustomAmount] = useState('');

  const isNextButtonEnabled =
    (selectAmount !== 'custom' && Number(selectAmount) > 0) ||
    (selectAmount === 'custom' && Number(customAmount) > 0 && Number(customAmount) <= 50000);

  return (
    <>
      <Spacing size={30} />
      <ProgressBar progress={80} color="#3182f6" size="normal" />
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>인증을 놓치면 얼마를 낼까요?</Top.TitleParagraph>}
        subtitle1={<Top.SubtitleBadges items={[]} />}
        subtitle2={
          <Top.SubtitleParagraph>
            실패는 종료가 아니라 비용이에요{'\n'}
            다음 도전은 그대로 이어집니다
          </Top.SubtitleParagraph>
        }
      />
      <Spacing size={30} />
      <View style={{ paddingHorizontal: 16, gap: 12 }}>
        <View style={{ flexDirection: 'row', gap: 12, margin: 12 }}>
          {PENALTY_OPTIONS.slice(0, 2).map(({ label, value }) => (
            <View key={String(value)} style={{ flex: 1 }}>
              <Button
                type={selectAmount === value ? 'primary' : 'dark'}
                style={selectAmount === value ? 'fill' : 'weak'}
                display="block"
                onPress={() => setSelectAmount(value)}
              >
                {label}
              </Button>
            </View>
          ))}
        </View>

        <View style={{ flexDirection: 'row', gap: 12, margin: 12 }}>
          {PENALTY_OPTIONS.slice(2, 4).map(({ label, value }) => (
            <View key={String(value)} style={{ flex: 1 }}>
              <Button
                type={selectAmount === value ? 'primary' : 'dark'}
                style={selectAmount === value ? 'fill' : 'weak'}
                display="block"
                onPress={() => setSelectAmount(value)}
              >
                {label}
              </Button>
            </View>
          ))}
        </View>

        {selectAmount === 'custom' && (
          <View style={{ paddingHorizontal: 16, marginTop: 12 }}>
            <TextField
              variant="box"
              label="금액 입력"
              labelOption="sustain"
              value={customAmount}
              onChangeText={setCustomAmount}
              placeholder="원하는 금액을 입력하세요"
              keyboardType="numeric"
            />
            {Number(customAmount) > 50000 && (
              <Txt
                typography="t6"
                color={adaptive.red500}
                style={{ textAlign: 'right', marginTop: 12, paddingHorizontal: 0 }}
              >
                최대 50,000원 까지만 설정할 수 있어요
              </Txt>
            )}
          </View>
        )}
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
              onPress={() => navigation.navigate('/create-challenge/step6')}
            >
              이전
            </Button>
          }
          rightButton={
            <Button
              type="primary"
              style="fill"
              display="block"
              disabled={!isNextButtonEnabled}
              loading={false}
              onPress={() => {
                updateData({
                  penaltyAmount: selectAmount,
                  customAmount: selectAmount === 'custom' ? customAmount : undefined,
                });
                navigation.navigate('/create-challenge/step8');
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
