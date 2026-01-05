import { ProgressBar, Top, Button, ListRow, FixedBottomCTA, FixedBottomCTAProvider } from '@toss/tds-react-native';
import { createRoute, Spacing } from '@granite-js/react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { View } from 'react-native';
import { useState } from 'react';
import { useCreateChallengeStore } from '../../src/stores/createChallengeStore';

export const Route = createRoute('/create-challenge/step6', {
  component: Page,
});

export default function Page() {
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();
  const updateData = useCreateChallengeStore((s) => s.updateData);

  const [selectedMethod, setSelectedMethod] = useState('사진');

  return (
    <>
      <Spacing size={30} />
      <ProgressBar progress={60} color="#3182f6" size="normal" />
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>어떻게 인증할까요?</Top.TitleParagraph>}
        subtitle1={<Top.SubtitleBadges items={[]} />}
        subtitle2={
          <Top.SubtitleParagraph>
            가장 부담 없는 방식이면 충분해요{'\n'}
            증명보다 지속이 중요해요
          </Top.SubtitleParagraph>
        }
      />
      <Spacing size={32} />
      <View style={{ flexDirection: 'row', alignSelf: 'center', gap: 12, paddingHorizontal: 24 }}>
        <Button
          size="large"
          style={selectedMethod === '사진' ? 'fill' : 'weak'}
          type={selectedMethod === '사진' ? 'primary' : 'dark'}
          onPress={() => setSelectedMethod('사진')}
        >
          사진
        </Button>
        <Button
          size="large"
          style={selectedMethod === '텍스트' ? 'fill' : 'weak'}
          type={selectedMethod === '텍스트' ? 'primary' : 'dark'}
          onPress={() => setSelectedMethod('텍스트')}
          disabled={true}
        >
          텍스트
        </Button>
        <Button
          size="large"
          style={selectedMethod === '체크' ? 'fill' : 'weak'}
          type={selectedMethod === '체크' ? 'primary' : 'dark'}
          onPress={() => setSelectedMethod('체크')}
          disabled={true}
        >
          체크
        </Button>
      </View>
      <Spacing size={40} />
      <ListRow
        left={<ListRow.ImageContainer type="square" style={{}} />}
        contents={
          <ListRow.Texts
            type="1RowTypeA"
            top="현재는 사진으로만 인증 가능해요"
            topProps={{ color: adaptive.grey700 }}
          />
        }
        verticalPadding={8}
      />
      {/*<TextField
        variant="box"
        label=""
        labelOption="sustain"
        value=""
        placeholder="사진/텍스트"
        editable={false}
        right={
          <>
            <Asset.Icon frameShape={Asset.frameShape.CleanW24} name="icon-arrow-down-mono" color={adaptive.grey400} />
          </>
        }
      />*/}
      <FixedBottomCTAProvider>
        <FixedBottomCTA.Double
          leftButton={
            <Button
              type="dark"
              style="weak"
              display="block"
              disabled={false}
              loading={false}
              onPress={() => navigation.navigate('/create-challenge/step5')}
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
                const methodMapping: { [key: string]: string } = {
                  사진: 'PHOTO',
                  텍스트: 'TEXT',
                  체크: 'VOTE',
                };
                updateData({
                  verificationType: methodMapping[selectedMethod],
                });
                navigation.navigate('/create-challenge/step7');
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
