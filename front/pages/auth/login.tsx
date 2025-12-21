import {Asset, Top, FixedBottomCTA, FixedBottomCTAProvider, Button} from '@toss/tds-react-native';
import { createRoute, Spacing } from '@granite-js/react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import { appLogin } from '@apps-in-toss/framework';
import { useState } from 'react';

export const Route = createRoute('/auth/login', {
    component: Page,
});

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    const [loading, setLoading] = useState(false);

    const handleLogin = async () => {
        try {
            setLoading(true);
            const { authorizationCode, referrer } = await appLogin();

            // TODO: authorizationCode를 서버로 전달하여 JWT 토큰 발급
            console.log('Login Success:', { authorizationCode, referrer });

            // 로그인 성공 후 메인 페이지로 이동
            navigation.navigate('/');
        } catch (error) {
            console.error('Login Failed:', error);
            setLoading(false);
        }
    };
    return (
        <>
            <Spacing size={14} />
            <Top
                title={
                    <Top.TitleParagraph size={28} color={adaptive.grey900}>
                        토스 계정으로{'\n'}
                        페이널티에 로그인할게요!
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
                <FixedBottomCTA.Double
                    leftButton={
                        <Button
                            type="primary"
                            style="fill"
                            display="block"
                            loading={loading}
                            onPress={handleLogin}
                        >
                            토스로 로그인하기
                        </Button>
                    }
                    rightButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            onPress={() => navigation.navigate('/')}
                        >
                            로그인 없이 둘러보기
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}