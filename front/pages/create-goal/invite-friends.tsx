import {Post, List, ListRow, Icon, FixedBottomCTA, FixedBottomCTAProvider, Button,} from '@toss/tds-react-native';
import {Paragraph, useAdaptive} from '@toss/tds-react-native/private';
import {createRoute, Spacing} from "@granite-js/react-native";

export const Route = createRoute('/create-goal/invite-friends', {
    component: Page,
})

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    return (
        <>
            <Spacing size={30}/>
            <Post.Paragraph
                paddingBottom={8}
                typography="t7"
                color={adaptive.grey600}
            >
                <Paragraph.Text>추가한 친구</Paragraph.Text>
            </Post.Paragraph>
            <List rowSeparator="none">
                <ListRow
                    contents={
                        <ListRow.Texts
                            type="1RowTypeB"
                            top="홍길동"
                            topProps={{color: adaptive.grey700}}
                        />
                    }
                    right={
                        <Icon name="icon-chip-x-mono" color={adaptive.grey300} size={24}/>
                    }
                />
                <ListRow
                    contents={
                        <ListRow.Texts
                            type="1RowTypeB"
                            top="김민수"
                            topProps={{color: adaptive.grey700}}
                        />
                    }
                    right={
                        <Icon name="icon-chip-x-mono" color={adaptive.grey300} size={24}/>
                    }
                />
                <ListRow
                    contents={
                        <ListRow.Texts
                            type="1RowTypeB"
                            top="이지은"
                            topProps={{color: adaptive.grey700}}
                        />
                    }
                    right={
                        <Icon name="icon-chip-x-mono" color={adaptive.grey300} size={24}/>
                    }
                />
                <ListRow
                    contents={
                        <ListRow.Texts
                            type="1RowTypeB"
                            top="박현우"
                            topProps={{color: adaptive.grey700}}
                        />
                    }
                    right={
                        <Icon name="icon-chip-x-mono" color={adaptive.grey300} size={24}/>
                    }
                />
                <ListRow
                    contents={
                        <ListRow.Texts
                            type="1RowTypeB"
                            top="김태훈"
                            topProps={{color: adaptive.grey700}}
                        />
                    }
                    right={
                        <Icon name="icon-chip-x-mono" color={adaptive.grey300} size={24}/>
                    }
                />
            </List>
            <FixedBottomCTAProvider>
                <FixedBottomCTA.Double
                    leftButton={
                        <Button
                            type="dark"
                            style="weak"
                            display="block"
                            disabled={false}
                            loading={false}
                            onPress={() => navigation.navigate("/create-goal/step8")}
                        >
                            이전
                        </Button>
                    }
                    rightButton={
                        <Button
                            type="primary"
                            style="fill"
                            display="block"
                            disabled={false}
                            loading={false}
                            onPress={() => navigation.navigate("/create-goal/complete")}
                        >
                            다음
                        </Button>
                    }
                />
            </FixedBottomCTAProvider>
        </>
    );
}