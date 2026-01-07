import { BottomSheet, List, ListRow, Asset } from '@toss/tds-react-native';
import { useOverlay, useAdaptive } from '@toss/tds-react-native/private';
import { View, Pressable, Alert } from 'react-native';
import { getSelectedChallenge } from '../stores/challengeStore';
import {
  openCamera,
  fetchAlbumPhotos,
  OpenCameraPermissionError,
  FetchAlbumPhotosPermissionError,
} from '@apps-in-toss/framework';
import { createVerification } from '../api/verifications';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { useLatestVerification } from './useVerifications';
import { getTimeDate, isTodayChallenge } from '../utils/challenge';

export function useVerificationModal() {
  const overlay = useOverlay();
  const adaptive = useAdaptive();
  const selectedChallenge = getSelectedChallenge();
  const queryClient = useQueryClient();

  // 가장 최근 인증 내역 조회
  const { data: latestVerification } = useLatestVerification(selectedChallenge?.id || '');

  // 인증 가능 상태 확인
  const getVerificationStatus = () => {
    if (!selectedChallenge) return { canVerify: false, reason: '선택된 챌린지가 없습니다.' };

    const now = new Date();
    const today = now.getTime();

    // 1. 챌린지 기간 확인 (마감 여부)
    if (selectedChallenge.endAt) {
      const endDate = new Date(selectedChallenge.endAt);
      endDate.setHours(23, 59, 59, 999);
      if (now > endDate) {
        return { canVerify: false, reason: '이미 종료된 챌린지입니다.' };
      }
    }

    // 2. 인증 요일 및 횟수 확인 (정석 로직 적용)
    const isToday = isTodayChallenge(
      selectedChallenge.daysOfWeek,
      Number(selectedChallenge.weeklyRequiredCount || 0),
      selectedChallenge.weeklyProgressCount || 0
    );

    if (!isToday) {
      return { canVerify: false, reason: '오늘은 인증 요일이 아니거나, 이미 주간 목표를 달성했습니다.' };
    }

    // 3. 중복 인증 확인
    const latestDateTime = latestVerification?.dateTime;
    const latestDate = latestDateTime ? new Date(latestDateTime) : null;
    const isAlreadyVerifiedToday =
      latestDate &&
      latestDate.getFullYear() === now.getFullYear() &&
      latestDate.getMonth() === now.getMonth() &&
      latestDate.getDate() === now.getDate();

    if (isAlreadyVerifiedToday) {
      return { canVerify: false, reason: '오늘은 이미 인증을 완료했습니다.\n내일 다시 도전해주세요!' };
    }

    // 4. 인증 시간 확인 (유틸리티 활용)
    if (selectedChallenge.verifyStart && selectedChallenge.verifyEnd) {
      const startTime = getTimeDate(selectedChallenge!.verifyStart).getTime();
      const endTime = getTimeDate(selectedChallenge!.verifyEnd).getTime();

      if (today < startTime) {
        return {
          canVerify: false,
          reason: `아직 인증 시간이 아닙니다.\n(인증 가능 시간: ${selectedChallenge!.verifyStart} ~ ${selectedChallenge!.verifyEnd})`,
        };
      }
      if (today > endTime) {
        return { canVerify: false, reason: '오늘 인증 시간이 마감되었습니다.\n내일 다시 도전해주세요!' };
      }
    }

    return { canVerify: true };
  };

  // 인증 생성 mutation
  const createVerificationMutation = useMutation({
    mutationFn: async (imageUri: string) => {
      if (!selectedChallenge?.id) {
        throw new Error('선택된 챌린지가 없습니다.');
      }
      
      const formData = new FormData();
      formData.append('image', {
        uri: imageUri,
        name: `verification_${Date.now()}.jpg`,
        type: 'image/jpeg',
      } as any);

      return await createVerification(selectedChallenge!.id, formData);
    },
    onSuccess: () => {
      // 관련 쿼리 무효화하여 자동 리페칭
      queryClient.invalidateQueries({ queryKey: ['latestVerification'] });
      queryClient.invalidateQueries({ queryKey: ['verifications'] });
      queryClient.invalidateQueries({ queryKey: ['memberVerificationCounts'] });
      queryClient.invalidateQueries({ queryKey: ['challenges'] });
      Alert.alert('인증 성공', '인증이 완료되었습니다.');
    },
    onError: (error: Error) => {
      Alert.alert('인증 실패', error.message || '인증에 실패했습니다.');
    },
  });


  const open = () => {
    const { canVerify, reason } = getVerificationStatus();

    if (!canVerify) {
      Alert.alert('인증 불가', reason);
      return;
    }

    overlay.open(({ isOpen, close, exit }) => {
      return (
        <BottomSheet.Root
          header={<BottomSheet.Header>인증 방법을 선택해주세요</BottomSheet.Header>}
          // 가이드라인 생성 시
          /*headerDescription={
            selectedChallenge?.guideline ? (
              <BottomSheet.HeaderDescription>{selectedChallenge.guideline}</BottomSheet.HeaderDescription>
            ) : undefined
          }*/
          open={isOpen}
          onClose={close}
          onExited={exit}
        >
          <View style={{ paddingVertical: 8, paddingBottom: 32 }}>
            <List rowSeparator="none">
              <Pressable
                disabled={createVerificationMutation.isPending}
                onPress={async () => {
                  try {
                    const result = await openCamera({ base64: true, maxWidth: 1024 });

                    if (!result) {
                      return;
                    }

                    console.log('Camera Success:', result.id);

                    const imageUri = `data:image/jpeg;base64,${result.dataUri}`;
                    
                    // 이미지 업로드 및 인증 생성
                    await createVerificationMutation.mutateAsync(imageUri);

                    close();
                  } catch (error) {
                    if (error instanceof OpenCameraPermissionError) {
                      Alert.alert('권한 오류', '카메라 권한이 거부되었습니다. 설정에서 권한을 허용해주세요.');
                    } else {
                      console.error('사진 가져오기 실패:', error);
                    }
                  }
                }}
              >
                <ListRow
                  left={
                    <View style={{ marginRight: 12 }}>
                      <Asset.Icon
                        frameShape={{ width: 24, height: 24 }}
                        name="icon-camera-mono"
                        color={adaptive.grey600}
                      />
                    </View>
                  }
                  contents={
                    <ListRow.Texts
                      type="1RowTypeA"
                      top="사진 촬영하기"
                      topProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
                    />
                  }
                  verticalPadding="small"
                />
              </Pressable>
              <Pressable
                disabled={createVerificationMutation.isPending}
                onPress={async () => {
                  try {
                    const result = await fetchAlbumPhotos({
                      maxCount: 1,
                      maxWidth: 1024,
                      base64: true,
                    });
                    const firstPhoto = result?.[0];
                    console.log('Album Success:', firstPhoto?.id || 'no images');

                    if (firstPhoto) {
                      const imageUri = `data:image/jpeg;base64,${firstPhoto.dataUri}`;
                      
                      // 이미지 업로드 및 인증 생성
                      await createVerificationMutation.mutateAsync(imageUri);

                      close();
                    }
                  } catch (error) {
                    if (error instanceof FetchAlbumPhotosPermissionError) {
                      Alert.alert('권한 오류', '사진첩 접근 권한이 거부되었습니다. 설정에서 권한을 허용해주세요.');
                    } else {
                      console.error('앨범 가져오기 실패:', error);
                    }
                  }
                }}
              >
                <ListRow
                  left={
                    <View style={{ marginRight: 12 }}>
                      <Asset.Icon
                        frameShape={{ width: 24, height: 24 }}
                        name="icon-picture-mono"
                        color={adaptive.grey600}
                      />
                    </View>
                  }
                  contents={
                    <ListRow.Texts
                      type="1RowTypeA"
                      top="앨범에서 선택하기"
                      topProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
                    />
                  }
                  verticalPadding="small"
                />
              </Pressable>
            </List>
          </View>
        </BottomSheet.Root>
      );
    });
  };

  return { open };
}
