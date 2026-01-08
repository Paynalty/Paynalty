import {BottomSheet, Agreement, TextButton} from '@toss/tds-react-native';
import {useAdaptive, useOverlay} from '@toss/tds-react-native/private';

export default function Page() {
    const adaptive = useAdaptive();
    const overlay = useOverlay();
    overlay.open(({ isOpen, close, exit }) => {
        return (
            <BottomSheet.Root
                header={
                    <BottomSheet.Header>
                        페이널티 로그인을 위해 꼭 필요한 동의만 추렸어요
                    </BottomSheet.Header>
                }
                open={isOpen}
                cta={
                    <BottomSheet.CTA
                        type="primary"
                        disabled={false}
                        size="xlarge"
                        style="fill"
                        bottomAccessory={
                            <TextButton
                                type="underline"
                                typography="st11"
                                color={adaptive.grey600}
                                fontWeight="medium"
                                onPress={() => {}}
                            >
                                다음에
                            </TextButton>
                        }
                    >
                        동의하고 시작하기
                    </BottomSheet.CTA>
                }
                onClose={close}
                onExited={exit}
            >
                <Agreement.Field type="medium-regular" indent={0} withBorder={false}>
                    페이널티 동의항목
                </Agreement.Field>
                <Agreement.Field
                    type="medium-regular"
                    indent={0}
                    arrow={<Agreement.Arrow />}
                    withBorder={false}
                >
                    동의항목은 콘솔에서 수정할 수 있어요
                </Agreement.Field>
                <Agreement.Field
                    type="medium-regular"
                    indent={0}
                    arrow={<Agreement.Arrow />}
                    withBorder={false}
                >
                    이 화면은 확인용으로만 사용해주세요
                </Agreement.Field>
                <Agreement.Field
                    type="medium-regular"
                    indent={0}
                    arrow={<Agreement.Arrow />}
                    withBorder={false}
                ></Agreement.Field>
                <Agreement.Field
                    type="medium-regular"
                    indent={0}
                    arrow={<Agreement.Arrow />}
                    withBorder={false}
                >
                    토스 동의항목
                </Agreement.Field>
                <Agreement.Field
                    type="medium-regular"
                    indent={0}
                    arrow={<Agreement.Arrow />}
                    withBorder={false}
                >
                    [필수] 개인정보 제3자 정보 제공
                </Agreement.Field>
                <Agreement.Field
                    type="medium-regular"
                    indent={0}
                    arrow={<Agreement.Arrow />}
                    withBorder={false}
                >
                    [선택] 선택 제공 항목
                </Agreement.Field>
            </BottomSheet.Root>
        );
    });
}