import { createRoute, useNavigation, Spacing } from '@granite-js/react-native';
import {
  Top,
  List,
  ListRow,
  FixedBottomCTA,
  FixedBottomCTAProvider,
  Txt,
  ListHeader,
} from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { Linking, Alert, View, ScrollView } from 'react-native';
import { useChallengeStore } from '../../src/stores/challengeStore';

export const Route = createRoute('/penalty-history', {
  component: Page,
});

// 토스 송금 실행 함수
const requestTossPayment = async (amount: number, bankCode: string, accountNo: string) => {
  const url = `supertoss://send?amount=${amount}&bankCode=${bankCode}&accountNo=${accountNo}`;

  try {
    const supported = await Linking.canOpenURL(url);

    if (supported) {
      await Linking.openURL(url);
    } else {
      Alert.alert('알림', '토스 앱이 설치되어 있지 않습니다. 스토어로 이동하시겠습니까?', [
        { text: '취소' },
        {
          text: '이동',
          onPress: () => Linking.openURL('https://toss.im/_m/L7p7p7'),
        },
      ]);
    }
  } catch (error) {
    console.error('Link 호출 중 오류 발생:', error);
    Alert.alert('오류', '송금 요청 중 문제가 발생했습니다.');
  }
};

// 샘플 패널티 데이터 (추후 API 연동 필요)
const DUMMY_PENALTIES = [
  { id: 1, date: '2024.01.05', reason: '인증 미완료', amount: 10000, status: 'UNPAID' },
  { id: 2, date: '2024.01.03', reason: '인증 미완료', amount: 10000, status: 'PAID' },
  { id: 3, date: '2023.12.30', reason: '시간 초과', amount: 10000, status: 'PAID' },
];

export default function Page() {
  const adaptive = useAdaptive();
  const selectedChallenge = useChallengeStore((s) => s.selectedChallengeObject);

  const unpaidTotal = DUMMY_PENALTIES
    .filter(p => p.status === 'UNPAID')
    .reduce((sum, p) => sum + p.amount, 0);

  return (
    <FixedBottomCTAProvider>
      <ScrollView style={{ backgroundColor: adaptive.grey50 }}>
        <Top
          title={
            <Top.TitleParagraph color={adaptive.grey900}>
              패널티 이력
            </Top.TitleParagraph>
          }
          subtitle2={
            <Top.SubtitleParagraph color={adaptive.grey600}>
              {selectedChallenge?.title}
            </Top.SubtitleParagraph>
          }
        />

        <View style={{ padding: 16 }}>
          <View style={{ 
            backgroundColor: '#ffffff', 
            padding: 20, 
            borderRadius: 16,
            shadowColor: '#000',
            shadowOffset: { width: 0, height: 2 },
            shadowOpacity: 0.05,
            shadowRadius: 8,
            elevation: 2
          }}>
            <Txt typography="t6" color={adaptive.grey600}>미납된 벌금</Txt>
            <Spacing size={4} />
            <Txt typography="t3" fontWeight="bold" color={adaptive.grey900}>
              {unpaidTotal.toLocaleString()}원
            </Txt>
          </View>
        </View>

        <ListHeader
          title={
            <ListHeader.TitleParagraph color={adaptive.grey700}>내역</ListHeader.TitleParagraph>
          }
        />
        <List>
          {DUMMY_PENALTIES.map((penalty) => (
            <ListRow
              key={penalty.id}
              contents={
                <ListRow.Texts
                  type="2RowTypeB"
                  top={penalty.reason}
                  topProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
                  bottom={penalty.date}
                  bottomProps={{ color: adaptive.grey500 }}
                />
              }
              right={
                <View style={{ alignItems: 'flex-end' }}>
                  <Txt typography="t6" fontWeight="bold" color={penalty.status === 'UNPAID' ? adaptive.red500 : adaptive.grey600}>
                    {penalty.amount.toLocaleString()}원
                  </Txt>
                  <Txt typography="t7" color={penalty.status === 'UNPAID' ? adaptive.red500 : adaptive.grey400}>
                    {penalty.status === 'UNPAID' ? '미납' : '납부완료'}
                  </Txt>
                </View>
              }
              verticalPadding="small"
            />
          ))}
        </List>
      </ScrollView>

      {unpaidTotal > 0 && (
        <FixedBottomCTA
          type="primary"
          style="fill"
          onPress={() => requestTossPayment(unpaidTotal, '092', '1234567890')} // 샘플: 토스뱅크
        >
          {unpaidTotal.toLocaleString()}원 토스로 송금하기
        </FixedBottomCTA>
      )}
    </FixedBottomCTAProvider>
  );
}
