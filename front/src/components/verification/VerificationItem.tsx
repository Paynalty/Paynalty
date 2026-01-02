import {Asset, Txt} from '@toss/tds-react-native';
import {useAdaptive} from '@toss/tds-react-native/private';
import {StyleSheet, View} from 'react-native';
import {formatTime} from '../../utils/challenge';

interface VerificationItemProps {
    userName: string;
    userAvatar?: string;
    imageUrl: string;
    dateTime: string;
    showDivider?: boolean;
}

export function VerificationItem({userName, userAvatar, imageUrl, dateTime, showDivider}: VerificationItemProps) {
    const adaptive = useAdaptive();

    return (
        <View
            style={[
                styles.container,
                showDivider && {borderTopWidth: 1, borderTopColor: adaptive.grey100, paddingTop: 16, marginTop: 16},
            ]}
        >
            <View style={styles.userSection}>
                <Asset.Image
                    frameShape={{width: 32, height: 32}}
                    source={{uri: userAvatar || 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                />
                <Txt color={adaptive.grey700} typography="t5" fontWeight="bold">
                    {userName}
                </Txt>
            </View>
            <View style={styles.imageSection}>
                <Asset.Image
                    frameShape={{height: 300}}
                    source={{uri: imageUrl}}
                    style={{width: '100%', height: 300, borderRadius: 12}}
                />
            </View>
            <View style={styles.bottomSection}>
                <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                    이의제기
                </Txt>
                <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                    {formatTime(dateTime)}
                </Txt>
            </View>
        </View>
    );
}

const styles = StyleSheet.create({
    container: {
        width: '100%',
    },
    userSection: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 8,
        marginBottom: 16,
    },
    imageSection: {
        marginBottom: 12,
    },
    bottomSection: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
    },
});
