import {
    Post,
    List,
    ListRow,
    Icon,
    FixedBottomCTA,
    FixedBottomCTAProvider,
    Button,
    SearchField,
} from '@toss/tds-react-native';
import {Paragraph, useAdaptive} from '@toss/tds-react-native/private';
import {createRoute, Spacing} from "@granite-js/react-native";
import {useState} from 'react';

export const Route = createRoute('/create-goal/invite-friends', {
    component: Page,
})

export default function Page() {
    const adaptive = useAdaptive();
    const navigation = Route.useNavigation();
    const [searchText, setSearchText] = useState('');
    const [filteredResults, setFilteredResults] = useState([]);
    const handleSearch = (text: string) => {
        setSearchText(text);
        // 검색 로직
        const results = data.filter(item =>
            item.name.toLowerCase().includes(text.toLowerCase())
        );
        setFilteredResults(results);
    };
    return (
        <>
            <Spacing size={30}/>
            <SearchField
                placeholder="친구의 이름, 이메일을 입력해요"
                autoFocus={true}
                value={searchText}
                hasClearButton={true}
                maxLength={40}
                onChange={(e) => setSearchText(e.nativeEvent.text)}
                style={{borderRadius: 20, marginHorizontal: 16}}
            />
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
                    verticalPadding="small"
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
                    verticalPadding="small"
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
                    verticalPadding="small"
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
                    verticalPadding="small"
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
                    verticalPadding="small"
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