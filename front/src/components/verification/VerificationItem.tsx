import {Asset, Icon, Txt} from '@toss/tds-react-native';
import {useEffect, useMemo, useState} from 'react';
import {useAdaptive} from '@toss/tds-react-native/private';
import {Alert, Image, StyleSheet, TouchableOpacity, View} from 'react-native';
import {formatTime, getFileUrl, getTimeDate} from '../../utils/challenge';
import {deleteVerification} from '../../api/verifications';
import {useQueryClient} from '@tanstack/react-query';

interface VerificationItemProps {
    verificationId: number;
    userName: string;
    userAvatar?: string;
    imageUrl: string;
    dateTime: string;
    showDivider?: boolean;
    imageHeight?: number;
    isMine: boolean;
}

export function VerificationItem({
    verificationId,
    userName, 
    userAvatar, 
    imageUrl, 
    dateTime, 
    showDivider, 
    imageHeight,
    isMine 
}: VerificationItemProps) {
    const adaptive = useAdaptive();
    const [aspectRatio, setAspectRatio] = useState(1);
    const queryClient = useQueryClient();

    const isToday = useMemo(() => {
        const d = getTimeDate(dateTime);
        const t = new Date();
        return d.getFullYear() === t.getFullYear() &&
               d.getMonth() === t.getMonth() &&
               d.getDate() === t.getDate();
    }, [dateTime]);

    useEffect(() => {
        if (!imageHeight && imageUrl) {
            Image.getSize(getFileUrl(imageUrl), (width, height) => {
                setAspectRatio(width / height);
            }, (error) => {
                console.error('Failed to get image size:', error);
            });
        }
    }, [imageUrl, imageHeight]);

    const handleDelete = () => {
        Alert.alert(
            '인증 내역을 삭제할까요?',
            '마감 전까지 다시 인증하지 않으면 \n패널티를 받을 수 있어요.',
            [
                { text: '취소', style: 'cancel' },
                { 
                    text: '삭제', 
                    style: 'destructive', 
                    onPress: async () => {
                        try {
                            await deleteVerification(verificationId);
                            // 관련 쿼리 무효화 (리스트, 최신 인증, 카운트, 챌린지 상세(홈/리스트))
                            await queryClient.invalidateQueries({ queryKey: ['verifications'] });
                            await queryClient.invalidateQueries({ queryKey: ['latestVerification'] });
                            await queryClient.invalidateQueries({ queryKey: ['memberVerificationCounts'] });
                            await queryClient.invalidateQueries({ queryKey: ['challenges'] });

                            // Store 업데이트: 현재 챌린지 상세 정보를 새로 불러와서 verificationStatus 갱신
                            // useChallengeStore는 hook rule 때문에 컴포넌트 내부에서 함수로 가져와야 함 or props로 id 받기
                            const { getChallengeDetail } = require('../../api/challenges');
                            const { useChallengeStore } = require('../../stores/challengeStore');
                            // verificationId로 challengeId를 알 수 없으나, 
                            // 보통 VerificationItem은 ChallengeDetail 페이지 내에서 쓰이므로 
                            // 현재 selectedChallengeId를 Store에서 가져올 수 있음.
                            const currentChallengeId = useChallengeStore.getState().selectedChallengeObject?.id;
                            
                            if (currentChallengeId) {
                                const freshDetail = await getChallengeDetail(currentChallengeId);
                                useChallengeStore.getState().setSelectedChallenge(freshDetail);
                            }

                            Alert.alert('삭제 성공', '인증 내역이 삭제되었습니다.');
                        } catch (e) {
                            console.error('Failed to delete verification', e);
                            Alert.alert('삭제 실패', '인증 내역 삭제 중 오류가 발생했습니다.');
                        }
                    } 
                }
            ]
        );
    };

    return (
        <View
            style={[
                styles.container,
                showDivider && {borderTopWidth: 1, borderTopColor: adaptive.grey100, paddingTop: 16, marginTop: 16},
            ]}
        >
            <View style={styles.header}>
                <View style={styles.userSection}>
                    <Asset.Image
                        frameShape={{width: 32, height: 32}}
                        source={{uri: getFileUrl(userAvatar) || 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                    />
                    <Txt color={adaptive.grey700} typography="t5" fontWeight="bold">
                        {userName}
                    </Txt>
                </View>
                {isMine && isToday && (
                    <TouchableOpacity onPress={handleDelete} hitSlop={{top: 10, bottom: 10, left: 10, right: 10}}>
                        <Icon name="icon-system-x-outlined" color={adaptive.grey400} size={20} />
                    </TouchableOpacity>
                )}
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
    header: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginBottom: 16,
    },
    userSection: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 8,
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
