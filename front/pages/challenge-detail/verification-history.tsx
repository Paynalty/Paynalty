import { Asset, Txt, Top, ListHeader } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { ScrollView, View } from 'react-native';
import { VerificationGroup } from '../../src/components/verification/VerificationGroup';

export default function Page() {
  const adaptive = useAdaptive();

  // 목 데이터: 날짜별로 그룹화된 형태
  const groupedVerifications = [
    {
      date: new Date().toISOString(),
      items: [
        {
          id: 1,
          userName: '지은',
          imageUrl: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png',
          dateTime: new Date().toISOString(),
        },
        {
          id: 2,
          userName: '민수',
          imageUrl: 'https://static.toss.im/ml-product/observer-binocular.png',
          dateTime: new Date(Date.now() - 1000 * 60 * 30).toISOString(),
        },
      ],
    },
    {
      date: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
      items: [
        {
          id: 3,
          userName: '현지',
          imageUrl: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png',
          dateTime: new Date(Date.now() - 1000 * 60 * 60 * 24).toISOString(),
        },
      ],
    },
  ];

  return (
    <View style={{ flex: 1, backgroundColor: adaptive.grey50 }}>
      <ScrollView>
        <Top
          title={<Top.TitleParagraph color={adaptive.grey900}>매일 만보 걷기</Top.TitleParagraph>}
          subtitle2={<Top.SubtitleParagraph color={adaptive.grey600}>하트브레이커 6명과 도전 중</Top.SubtitleParagraph>}
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
              최근 인증 현황
            </ListHeader.TitleParagraph>
          }
        />

        {groupedVerifications.map((group) => (
          <VerificationGroup key={group.date} date={group.date} verifications={group.items} />
        ))}

        <View style={{ height: 32 }} />
      </ScrollView>
    </View>
  );
}
