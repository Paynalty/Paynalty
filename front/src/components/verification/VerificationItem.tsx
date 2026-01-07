import {Asset, Txt} from '@toss/tds-react-native';
import {useEffect, useState} from 'react';
import {useAdaptive} from '@toss/tds-react-native/private';
import {Image, StyleSheet, View} from 'react-native';
import {formatTime, getFileUrl} from '../../utils/challenge';

interface VerificationItemProps {
    userName: string;
    userAvatar?: string;
    imageUrl: string;
    dateTime: string;
    showDivider?: boolean;
    imageHeight?: number;
}

export function VerificationItem({userName, userAvatar, imageUrl, dateTime, showDivider, imageHeight}: VerificationItemProps) {
    const adaptive = useAdaptive();
    const [aspectRatio, setAspectRatio] = useState(1);

    useEffect(() => {
        if (!imageHeight && imageUrl) {
            Image.getSize(getFileUrl(imageUrl), (width, height) => {
                setAspectRatio(width / height);
            }, (error) => {
                console.error('Failed to get image size:', error);
            });
        }
    }, [imageUrl, imageHeight]);

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
                    source={{uri: getFileUrl(userAvatar) || 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                />
                <Txt color={adaptive.grey700} typography="t5" fontWeight="bold">
                    {userName}
                </Txt>
            </View>
            <View style={styles.imageSection}>
                <Image
                    source={{uri: getFileUrl(imageUrl)}}
                    style={[
                        { width: '100%', borderRadius: 12 },
                        imageHeight ? { height: imageHeight } : { aspectRatio }
                    ]}
                />
            </View>
            <View style={styles.bottomSection}>
                <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                    {/* 이의제기 */}
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
