import { createRoute, Spacing } from '@granite-js/react-native';
import {
  ProgressBar,
  Top,
  TextArea,
  FixedBottomCTA,
  FixedBottomCTAProvider,
  Button,
  Txt,
} from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { useState } from 'react';
import { View, Alert } from 'react-native';
import { useQueryClient } from '@tanstack/react-query';
import { useCreateChallengeStore } from '../../src/stores/createChallengeStore';
import { deleteChallenge } from '../../src/api/challenges';
import { challengeQueries } from '../../src/hooks/useChallenges';

export const Route = createRoute('/create-challenge/step2', {
  component: Page,
});

const MAX_TITLE_LENGTH = 30;

function Page() {
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();
  const queryClient = useQueryClient();
  const { data, updateData } = useCreateChallengeStore();
  const [goalTitle, setGoalTitle] = useState(data.title || '');

  // 챌린지 삭제 처리
  const handleDelete = () => {
    Alert.alert('챌린지를 삭제할까요?', '삭제하면 복구할 수 없어요.', [
      { text: '취소', style: 'cancel' },
      {
        text: '삭제',
        style: 'destructive',
        onPress: async () => {
          try {
            if (data.challengeId) {
              await deleteChallenge(data.challengeId);
              await queryClient.invalidateQueries({ queryKey: challengeQueries.all });
              navigation.navigate('/');
              alert('챌린지가 삭제되었습니다.');
            }
          } catch (error) {
            console.error('삭제 실패:', error);
            alert('삭제에 실패했습니다.');
          }
        },
      },
    ]);
  };

  // 목표 제목 변경 처리
  const handleChangeTitle = (text: string) => {
    // 최대 길이 제한
    if (text.length <= MAX_TITLE_LENGTH) {
      setGoalTitle(text);
    }
  };

  // 다음 버튼 활성화 조건
  const isNextButtonEnabled = goalTitle.trim().length > 0;

  return (
    <>
      <Spacing size={30} />
      <ProgressBar progress={20} color="#3182f6" size="normal" />
      <Top
        title={<Top.TitleParagraph color={adaptive.grey900}>어떤 목표를 달성하고 싶나요?</Top.TitleParagraph>}
        subtitle1={<Top.SubtitleBadges items={[]} />}
        subtitle2={<Top.SubtitleParagraph>하고 싶은 일을 정해요</Top.SubtitleParagraph>}
        lowerGap={0}
      />
      <View style={{ paddingHorizontal: 16 }}>
        <TextArea
          label=""
          value={goalTitle}
          placeholder="어떤 목표든 괜찮아요!"
          autoFocus={true}
          onChangeText={handleChangeTitle}
        />
        {goalTitle.length >= 20 && (
          <Txt
            typography="t6"
            color={adaptive.red500}
            style={{ textAlign: 'right', marginTop: 0, paddingHorizontal: 28 }}
          >
            {`최대 ${goalTitle.length}/${MAX_TITLE_LENGTH}자 까지만 작성할 수 있어요`}
          </Txt>
        )}
      </View>
      <Spacing size={24} />

      {/* TODO : 템플릿 제공*/}
      <FixedBottomCTAProvider>
        {data.isEditing ? (
          <FixedBottomCTA.Double
            leftButton={
              <Button type="danger" style="weak" display="block" loading={false} onPress={handleDelete}>
                삭제하기
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
                  updateData({ title: goalTitle.trim() });
                  navigation.navigate('/create-challenge/step3');
                }}
              >
                다음
              </Button>
            }
          />
        ) : (
          <FixedBottomCTA.Double
            leftButton={
              <Button
                type="dark"
                style="weak"
                display="block"
                disabled={false}
                loading={false}
                onPress={() => navigation.navigate('/create-challenge')}
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
                  updateData({ title: goalTitle.trim() });
                  navigation.navigate('/create-challenge/step3');
                }}
              >
                다음
              </Button>
            }
          />
        )}
      </FixedBottomCTAProvider>
    </>
  );
}
