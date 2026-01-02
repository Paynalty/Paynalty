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
import { useState } from 'react';

export function useVerificationModal() {
  const overlay = useOverlay();
  const adaptive = useAdaptive();
  const selectedChallenge = getSelectedChallenge();
  const queryClient = useQueryClient();
  const [isUploading, setIsUploading] = useState(false);

  // 인증 생성 mutation
  const createVerificationMutation = useMutation({
    mutationFn: async (imageUrl: string) => {
      if (!selectedChallenge?.id) {
        throw new Error('선택된 챌린지가 없습니다.');
      }
      // TODO: 로그인 구현 후 userId를 동적으로 가져오기
      const userId = 1;
      return await createVerification(selectedChallenge.id, userId, { imageUrl });
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

  // 이미지를 서버에 업로드하는 헬퍼 함수
  const uploadImage = async (imageId: string): Promise<string> => {
    setIsUploading(true);
    try {
      // TODO: 실제 S3 업로드 로직 구현 필요
      // 현재는 임시로 imageId를 URL처럼 반환
      return `https://example.com/images/${imageId}`;
    } finally {
      setIsUploading(false);
    }
  };

  const open = () => {
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
                disabled={isUploading || createVerificationMutation.isPending}
                onPress={async () => {
                  try {
                    const result = await openCamera({ base64: true, maxWidth: 1024 });
                    console.log('Camera Success:', result.id);

                    // 이미지 업로드 후 인증 생성
                    const imageUrl = await uploadImage(result.id);
                    await createVerificationMutation.mutateAsync(imageUrl);

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
                      top={isUploading ? '업로드 중...' : '사진 촬영하기'}
                      topProps={{ color: adaptive.grey800, fontWeight: 'bold' }}
                    />
                  }
                  verticalPadding="small"
                />
              </Pressable>
              <Pressable
                disabled={isUploading || createVerificationMutation.isPending}
                onPress={async () => {
                  try {
                    const result = await fetchAlbumPhotos({
                      maxCount: 1,
                      maxWidth: 1024,
                      base64: true,
                    });
                    console.log('Album Success:', result?.[0]?.id || 'no images');

                    if (result && result.length > 0) {
                      // 이미지 업로드 후 인증 생성
                      const imageUrl = await uploadImage(result[0].id);
                      await createVerificationMutation.mutateAsync(imageUrl);

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
                      top={isUploading ? '업로드 중...' : '앨범에서 선택하기'}
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
