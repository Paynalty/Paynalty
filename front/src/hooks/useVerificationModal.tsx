import {BottomSheet, Txt} from '@toss/tds-react-native';
import {useOverlay, useAdaptive} from '@toss/tds-react-native/private'
import {View} from 'react-native';

export function useVerificationModal() {
    const overlay = useOverlay();
    const adaptive = useAdaptive();

    const open = () => {
        overlay.open(({isOpen, close, exit}) => {
            return (
                <BottomSheet.Root
                    header={
                        <BottomSheet.Header>인증 사진을 선택해주세요</BottomSheet.Header>
                    }
                    headerDescription={
                        <BottomSheet.HeaderDescription>
                            가이드라인은 이렇게 되어있어요!
                        </BottomSheet.HeaderDescription>
                    }
                    open={isOpen}
                    onClose={close}
                    onExited={exit}
                >
                    <View style={{padding: 16}}>
                        <Txt color={adaptive.grey600}>사진 촬영하기</Txt>
                        <Txt color={adaptive.grey600}>앨범에서 선택하기</Txt>
                    </View>
                </BottomSheet.Root>
            );
        })
    }

    return { open }
}