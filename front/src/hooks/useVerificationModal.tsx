import { BottomSheet, List, ListRow, Asset } from '@toss/tds-react-native';
import { useOverlay, useAdaptive } from '@toss/tds-react-native/private';
import { View, Pressable } from 'react-native';
import { getSelectedChallenge } from '../stores/challengeStore';

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
                onPress={() => {
                  console.log('Camera');
                  close();
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
                onPress={() => {
                  console.log('Album');
                  close();
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
