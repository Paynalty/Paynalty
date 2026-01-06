import { createRoute, Spacing } from '@granite-js/react-native';
import { Asset, FixedBottomCTA, FixedBottomCTAProvider, Button, List, ListRow, Top } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { useState } from 'react';
import { useCreateChallengeStore } from '../../src/stores/createChallengeStore';
import { createChallenge, updateChallenge, CreateChallengeRequestSchema } from '../../src/api/challenges';
import { formatDate, formatTime, getVerificationTypeLabel, formatDaysOfWeek } from '../../src/utils/challenge';
import { useQueryClient } from '@tanstack/react-query';
import { challengeQueries } from '../../src/hooks/useChallenges';
import { z } from 'zod';

export const Route = createRoute('/create-challenge/complete', {
  component: Page,
});

export default function Page() {
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();
  const queryClient = useQueryClient();
  type StartType = 'nextWeek' | 'tomorrow';
  const [loading, setLoading] = useState<StartType | null>(null);

  const { data: challengeData, resetData: resetChallengeData } = useCreateChallengeStore();

  // startDate 계산 함수
  const calculateStartDate = (option: 'tomorrow' | 'nextWeek'): string => {
    const today = new Date();
    const date = new Date(today);

    if (option === 'tomorrow') {
      date.setDate(date.getDate() + 1);
    } else if (option === 'nextWeek') {
      date.setDate(date.getDate() + 7);
    }

    // ISO date string (YYYY-MM-DD)
    return date.toISOString().split('T')[0] ?? '';
  };

  // API로 목표 생성 요청
  const handleCreateChallenge = async (option: 'tomorrow' | 'nextWeek') => {
    try {
      setLoading(option);

      const startDate = calculateStartDate(option);

      // 1. 전송할 데이터 구성
      const payload = {
        title: challengeData.title || '',
        startDate: challengeData.isEditing ? undefined : startDate,
        endDate: challengeData.endDate || '',
        penaltyAmount:
          challengeData.penaltyAmount === 'custom'
            ? Number(challengeData.customAmount || 0)
            : Number(challengeData.penaltyAmount || 0),
        frequency: challengeData.frequency,
        daysOfWeek: challengeData.daysOfWeek as any,
        verifyStartAt: challengeData.verifyStartAt,
        verifyEndAt: challengeData.verifyEndAt,
        verificationType: challengeData.verificationType as any,
        userIds: challengeData.invitedUsers?.map((u) => u.userId),
      };

      // 2. [Zod] 최종 제출 전 데이터 검증
      const validatedPayload = CreateChallengeRequestSchema.parse(payload);

      // 3. API 요청
      if (challengeData.isEditing && challengeData.challengeId) {
        await updateChallenge(challengeData.challengeId, validatedPayload as any);
        alert('목표를 수정했습니다.');
      } else {
        await createChallenge(validatedPayload as any);
        alert('목표를 만들었습니다.');
      }

      await queryClient.invalidateQueries({ queryKey: challengeQueries.all });

      // 성공 시 데이터 초기화
      resetChallengeData();
      // 메인 페이지로 이동
      navigation.navigate('/');
    } catch (error) {
      if (error instanceof z.ZodError) {
        console.error('Validation Error:', error.issues);
        alert('입력 정보가 올바르지 않습니다. 다시 확인해주세요.');
        return;
      }
      console.error('목표 생성 실패:', error);
      alert('목표 생성에 실패했습니다.');
    } finally {
      setLoading(null);
    }
  };
  return (
    <>
      {/* 컨텐츠 */}
      <Spacing size={30} />
      <Top
        upper={
          <Top.UpperAssetContent
            content={
              <Asset.Lottie
                frameShape={Asset.frameShape.SquareLarge}
                scale={1}
                autoPlay={true}
                loop={false}
                src="https://static.toss.im/lotties-common/check-blue-spot.json"
              />
            }
          />
        }
        title={<Top.TitleParagraph size={28}>목표를 만들었어요</Top.TitleParagraph>}
      />
      <List rowSeparator="none">
        <ListRow
          left={
            <ListRow.Image
              type="circle"
              source={{
                uri: 'https://static.toss.im/ml-product/square-salt-topped-saltbread.png',
              }}
            />
          }
          contents={
            <ListRow.Texts
              type="2RowTypeD"
              top="목표"
              topProps={{ color: adaptive.grey600 }}
              bottom={challengeData.title}
              bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
            />
          }
          verticalPadding="small"
        />
        <ListRow
          left={
            <ListRow.Image
              type="circle"
              source={{
                uri: 'https://static.toss.im/ml-product/yellow-slippers.png',
              }}
            />
          }
          contents={
            <ListRow.Texts
              type="2RowTypeD"
              top="마감일"
              topProps={{ color: adaptive.grey600 }}
              bottom={formatDate(challengeData.endDate || '')}
              bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
            />
          }
          verticalPadding="small"
        />
        <ListRow
          left={
            <ListRow.Image
              type="circle"
              source={{
                uri: 'https://static.toss.im/ml-product/workgloves-constructiongloves.png',
              }}
            />
          }
          contents={
            <ListRow.Texts
              type="2RowTypeD"
              top="인증 주기"
              topProps={{ color: adaptive.grey600 }}
              bottom={
                challengeData.daysOfWeek && challengeData.daysOfWeek.length > 0
                  ? `${formatDaysOfWeek(challengeData.daysOfWeek)} / 주 ${challengeData.frequency}회 / ${formatTime(challengeData.verifyStartAt || '')} ~ ${formatTime(challengeData.verifyEndAt || '')}`
                  : `주 ${challengeData.frequency}회 / ${formatTime(challengeData.verifyStartAt || '')} ~ ${formatTime(challengeData.verifyEndAt || '')}`
              }
              bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
            />
          }
          verticalPadding="small"
        />
        <ListRow
          left={
            <ListRow.Image
              type="circle"
              source={{
                uri: 'https://static.toss.im/ml-product/rubber-duck.png',
              }}
            />
          }
          contents={
            <ListRow.Texts
              type="2RowTypeD"
              top="인증 방법"
              topProps={{ color: adaptive.grey600 }}
              bottom={getVerificationTypeLabel(challengeData.verificationType)}
              bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
            />
          }
          verticalPadding="small"
        />
        <ListRow
          left={
            <ListRow.Image
              type="circle"
              source={{
                uri: 'https://static.toss.im/ml-product/squirrel-sitting-left.png',
              }}
            />
          }
          contents={
            <ListRow.Texts
              type="2RowTypeD"
              top="벌금"
              topProps={{ color: adaptive.grey600 }}
              bottom={
                challengeData.penaltyAmount === 'custom'
                  ? `${Number(challengeData.customAmount).toLocaleString()}원`
                  : `${Number(challengeData.penaltyAmount).toLocaleString()}원`
              }
              bottomProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
            />
          }
          verticalPadding="small"
        />
      </List>
      <FixedBottomCTAProvider>
        {challengeData.isEditing ? (
          <FixedBottomCTA
            type="primary"
            style="fill"
            loading={loading !== null}
            onPress={() => handleCreateChallenge()}
          >
            수정 완료
          </FixedBottomCTA>
        ) : (
          <FixedBottomCTA.Double
            leftButton={
              <Button
                type="dark"
                style="weak"
                display="block"
                disabled={loading !== null}
                loading={loading === 'nextWeek'}
                onPress={() => handleCreateChallenge('nextWeek')}
              >
                다음주부터 시작하기
              </Button>
            }
            rightButton={
              <Button
                type="primary"
                style="fill"
                display="block"
                disabled={loading !== null}
                loading={loading === 'tomorrow'}
                onPress={() => handleCreateChallenge('tomorrow')}
              >
                내일부터 시작하기
              </Button>
            }
          />
        )}
      </FixedBottomCTAProvider>
    </>
  );
}
