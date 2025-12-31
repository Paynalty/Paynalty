import { Asset, Txt, Top, ListHeader } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { ScrollView, View } from 'react-native';
import { VerificationGroup } from '../../src/components/verification/VerificationGroup';
import { useVerifications } from '../../src/hooks/useVerifications';
import { useChallengeStore } from '../../src/stores/challengeStore';

export default function Page() {
  const adaptive = useAdaptive();
  const selectedChallenge = useChallengeStore((s) => s.selectedChallengeObject);

  // challengeId가 number일 경우를 대비해 string으로 변환
  const challengeId = selectedChallenge?.id ? String(selectedChallenge.id) : '';
  const { data: verifications = [] } = useVerifications(challengeId);

  // 데이터를 날짜별로 그룹화 (YYYY-MM-DD 기준)
  const groupedVerifications = verifications.reduce(
    (acc, current) => {
      const dateKey = current.dateTime.split('T')[0];
      const existingGroup = acc.find((group) => group.date.startsWith(dateKey));

      if (existingGroup) {
        existingGroup.items.push({
          id: current.id,
          userName: current.userName,
          imageUrl: current.imageUrl,
          dateTime: current.dateTime,
        });
      } else {
        acc.push({
          date: current.dateTime,
          items: [
            {
              id: current.id,
              userName: current.userName,
              imageUrl: current.imageUrl,
              dateTime: current.dateTime,
            },
          ],
        });
      }
      return acc;
    },
    [] as { date: string; items: any[] }[]
  );

  return (
    <View style={{ flex: 1 }}>
      <ScrollView>
        <Top
          title={
            <Top.TitleParagraph color={adaptive.grey900}>{selectedChallenge?.title || '챌린지'}</Top.TitleParagraph>
          }
          subtitle2={
            <Top.SubtitleParagraph color={adaptive.grey600}>
              {selectedChallenge
                ? `${selectedChallenge.participants || ''} ${selectedChallenge.participantCount}명과 도전 중`
                : '정보 로딩 중...'}
            </Top.SubtitleParagraph>
          }
          right={
            <Top.UpperAssetContent
              content={
                <Asset.Image
                  frameShape={Asset.frameShape.CleanW60}
                  source={{
                    uri: 'https://static.toss.im/ml-product/observer-binocular.png',
                  }}
                />
              }
            />
          }
          lowerGap={0}
        />
        <ListHeader
          title={
            <ListHeader.TitleParagraph
              color={adaptive.grey800}
              fontWeight="bold"
              typography="t5"
              style={{ marginLeft: 16 }}
            >
              인증 현황
            </ListHeader.TitleParagraph>
          }
        />

        {groupedVerifications.length > 0 ? (
          groupedVerifications.map((group) => (
            <VerificationGroup key={group.date} date={group.date} verifications={group.items} />
          ))
        ) : (
          <View style={{ padding: 40, alignItems: 'center' }}>
            <Txt color={adaptive.grey500}>아직 인증 내역이 없습니다.</Txt>
          </View>
        )}

        <View style={{ height: 32 }} />
      </ScrollView>
    </View>
  );
}
