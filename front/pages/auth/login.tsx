import {Asset, Txt, Top, FixedBottomCTA, FixedBottomCTAProvider} from '@toss/tds-react-native';
import { Spacing } from '@granite-js/react-native';
import { useAdaptive } from '@toss/tds-react-native/private';

export default function Page() {
    const adaptive = useAdaptive();
    return (
        <>
            <>
                <Asset.Icon
                    frameShape={Asset.frameShape.CleanW24}
                    name="icon-arrow-back-ios-mono"
                    color="#191F28ff"
                />
            </>
            <Txt color="#191F28ff" typography="t6" fontWeight="semibold">
                페이널티
            </Txt>
            <>
                <Asset.Icon
                    frameShape={{ width: 20 }}
                    name="icon-dots-mono"
                    color="rgba(0, 19, 43, 0.58)"
                />
            </>
            <>
                <Asset.Icon
                    frameShape={{ width: 20 }}
                    name="icon-x-mono"
                    color="rgba(0, 19, 43, 0.58)"
                />
            </>
            <>
                <Asset.Image
                    frameShape={{ width: 16 }}
                    source={{
                        uri: 'https://static.toss.im/appsintoss/11149/88b088d4-24ea-4d39-9503-55e53608f7f1.png',
                    }}
                />
            </>
            <Spacing size={14} />
            <Top
                title={
                    <Top.TitleParagraph size={28} color={adaptive.grey900}>
                        페이널티에서 토스로 로그인할게요
                    </Top.TitleParagraph>
                }
                upper={
                    <Top.UpperAssetContent
                        content={
                            <Asset.Lottie
                                frameShape={Asset.frameShape.CleanW60}
                                src="https://static.toss.im/lotties-common/check-spot.json"
                                loop={false}
                            />
                        }
                    />
                }
            />
            <FixedBottomCTAProvider>
                <FixedBottomCTA loading={false} bottomAccessory="로그인 없이 둘러보기">
                    페이널티 시작하기
                </FixedBottomCTA>
            </FixedBottomCTAProvider>
        </>
    );
}