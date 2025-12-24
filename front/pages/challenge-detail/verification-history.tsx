import {Asset, Txt, Top, ListHeader, Post,} from '@toss/tds-react-native';
import {Paragraph, useAdaptive} from '@toss/tds-react-native/private';
import {ScrollView, StyleSheet, View} from "react-native";

export default function Page() {
    const adaptive = useAdaptive();
    return (
        <>
            <ScrollView>
                <Top
                    title={
                        <Top.TitleParagraph color={adaptive.grey900}>
                            매일 만보 걷기
                        </Top.TitleParagraph>
                    }
                    subtitle2={
                        <Top.SubtitleParagraph color={adaptive.grey600}>
                            하트브레이커 6명과 도전 중
                        </Top.SubtitleParagraph>
                    }
                    right={
                        <Top.UpperAssetContent
                            content={
                                <Asset.Image
                                    frameShape={Asset.frameShape.CleanW60}
                                    source={{
                                        uri: 'https://static.toss.im/ml-product/observer-binocular.png',
                                    }}
                                />
                            }
                        />
                    }
                    lowerGap={0}
                />
                <ListHeader
                    title={
                        <ListHeader.TitleParagraph
                            color={adaptive.grey800}
                            fontWeight="bold"
                            typography="t5"
                        >
                            최근 인증 현황
                        </ListHeader.TitleParagraph>
                    }
                    right={
                        <ListHeader.RightArrow typography={"t7"} color={adaptive.grey400}>
                            검색
                        </ListHeader.RightArrow>
                    }
                />
                <View style={styles.verificationCard}>
                    <View style={styles.dateSection}>
                        <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
                            2025년 8월 15일 월요일
                        </Txt>
                    </View>
                    <View style={styles.userSection}>
                        <Asset.Image
                            frameShape={{width: 32, height: 32}}
                            source={{uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                        />
                        <Txt color={adaptive.grey700} typography="t5" fontWeight="bold">
                            지은
                        </Txt>
                    </View>
                    <View style={styles.imageSection}>
                        <Asset.Image
                            frameShape={{width: 300, height: 300}}
                            source={{uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                        />
                    </View>
                    <View style={styles.bottomSection}>
                        <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                            이의제기
                        </Txt>
                        <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                            오전 10:58
                        </Txt>
                    </View>
                </View>

                {/* 밑에 반복*/}
                <View style={styles.verificationCard}>
                    <View style={styles.dateSection}>
                        <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
                            2025년 8월 15일 월요일
                        </Txt>
                    </View>
                    <View style={styles.userSection}>
                        <Asset.Image
                            frameShape={{width: 32, height: 32}}
                            source={{uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                        />
                        <Txt color={adaptive.grey700} typography="t5" fontWeight="bold">
                            지은
                        </Txt>
                    </View>
                    <View style={styles.imageSection}>
                        <Asset.Image
                            frameShape={{width: 300, height: 300}}
                            source={{uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                        />
                    </View>
                    <View style={styles.bottomSection}>
                        <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                            이의제기
                        </Txt>
                        <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                            오전 10:58
                        </Txt>
                    </View>
                </View>
                <View style={styles.verificationCard}>
                    <View style={styles.dateSection}>
                        <Txt color={adaptive.grey500} typography="st13" fontWeight="medium">
                            2025년 8월 15일 월요일
                        </Txt>
                    </View>
                    <View style={styles.userSection}>
                        <Asset.Image
                            frameShape={{width: 32, height: 32}}
                            source={{uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                        />
                        <Txt color={adaptive.grey700} typography="t5" fontWeight="bold">
                            지은
                        </Txt>
                    </View>
                    <View style={styles.imageSection}>
                        <Asset.Image
                            frameShape={{width: 300, height: 300}}
                            source={{uri: 'https://static.toss.im/ml-product/tosst-inapp_tdvjdh3nb4l5yg4xp9a734u4.png'}}
                        />
                    </View>
                    <View style={styles.bottomSection}>
                        <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                            이의제기
                        </Txt>
                        <Txt color={adaptive.grey500} typography="t7" fontWeight="medium">
                            오전 10:58
                        </Txt>
                    </View>
                </View>
                {/*여기까지*/}
            </ScrollView>
        </>
    );
}

const styles = StyleSheet.create({
    verificationCard: {
        padding: 16,
        marginHorizontal: 16,
        marginVertical: 8,
        backgroundColor: '#ffffff',
        borderRadius: 12,
        borderWidth: 1,
        borderColor: '#e0e0e0',
    },
    dateSection: {
        alignItems: 'center',
        marginBottom: 12,
    },
    userSection: {
        flexDirection: 'row',
        alignItems: 'center',
        gap: 8,
        marginBottom: 16,
    },
    imageSection: {
        alignItems: 'center',
        marginVertical: 16,
    },
    bottomSection: {
        flexDirection: 'row',
        justifyContent: 'space-between',
        alignItems: 'center',
        marginTop: 12,
    },
});
