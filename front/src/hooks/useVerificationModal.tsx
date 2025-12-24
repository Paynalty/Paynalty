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

export function useVerificationModal() {
  const overlay = useOverlay();
  const adaptive = useAdaptive();
  const selectedChallenge = getSelectedChallenge();

  const open = () => {
    overlay.open(({ isOpen, close, exit }) => {
      return (
        <BottomSheet.Root
          header={<BottomSheet.Header>인증 방법을 선택해주세요</BottomSheet.Header>}
          headerDescription={
            selectedChallenge?.guideline ? (
              <BottomSheet.HeaderDescription>{selectedChallenge.guideline}</BottomSheet.HeaderDescription>
            ) : undefined
          }
          open={isOpen}
          onClose={close}
          onExited={exit}
        >
          <View style={{ paddingVertical: 8, paddingBottom: 32 }}>
            <List rowSeparator="none">
              <Pressable
                onPress={async () => {
                  try {
                    const result = await openCamera({ base64: true, maxWidth: 1024 });
                    console.log('Camera Success:', result.id);
                    close();
                  } catch (error) {
                    if (error instanceof OpenCameraPermissionError) {
                      Alert.alert('권한 오류', '카메라 권한이 거부되었습니다. 설정에서 권한을 허용해주세요.');
                    }
                    console.error('사진을 가져오는 데 실패했어요:', error);
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
                onPress={async () => {
                  try {
                    const result = await fetchAlbumPhotos({
                      maxCount: 1,
                      maxWidth: 1024,
                      base64: true,
                    });
                    console.log('Album Success:', result?.[0]?.id || 'no images');
                    close();
                  } catch (error) {
                    if (error instanceof FetchAlbumPhotosPermissionError) {
                      Alert.alert('권한 오류', '사진첩 접근 권한이 거부되었습니다. 설정에서 권한을 허용해주세요.');
                    } else {
                      console.error('앨범을 가져오는 데 실패했어요:', error);
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
