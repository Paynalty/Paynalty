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
import { Paragraph, useAdaptive } from '@toss/tds-react-native/private';
import { createRoute, Spacing } from '@granite-js/react-native';
import { useState } from 'react';
import { View, ActivityIndicator, Pressable } from 'react-native';
import { useUserSearch } from '../../src/hooks/useUsers';
import { useCreateGoalStore } from '../../src/stores/createGoalStore';
import { UserResponse } from '../../src/api/users';

export const Route = createRoute('/create-goal/invite-friends', {
  component: Page,
});

export default function Page() {
  const adaptive = useAdaptive();
  const navigation = Route.useNavigation();
  const [searchText, setSearchText] = useState('');
  const [selectedUsers, setSelectedUsers] = useState<UserResponse[]>([]);
  const { updateData } = useCreateGoalStore();

  // 사용자 검색 Hook
  const { data: searchResults = [], isLoading } = useUserSearch(searchText);

  // 사용자 추가
  const handleAddUser = (user: UserResponse) => {
    if (!selectedUsers.find(u => u.userId === user.userId)) {
      setSelectedUsers([...selectedUsers, user]);
    }
  };

  // 사용자 제거
  const handleRemoveUser = (userId: number) => {
    setSelectedUsers(selectedUsers.filter(u => u.userId !== userId));
  };

  // 다음 단계로 이동 시 선택된 사용자 ID를 store에 저장
  const handleNext = () => {
    updateData({ invitedUsers: selectedUsers.map(u => u.userId) });
    navigation.navigate('/create-goal/complete');
  };

  return (
    <>
      <Spacing size={30} />
      <SearchField
        placeholder="친구의 이름, 이메일을 입력해요"
        autoFocus={true}
        value={searchText}
        hasClearButton={true}
        maxLength={40}
        onChange={(e) => setSearchText(e.nativeEvent.text)}
        style={{ borderRadius: 20, marginHorizontal: 16 }}
      />
      <Spacing size={30} />

      {/* 검색 결과 */}
      {searchText.length >= 2 && (
        <>
          <Post.Paragraph
            paddingBottom={8}
            typography="t7"
            color={adaptive.grey600}
          >
            <Paragraph.Text>검색 결과</Paragraph.Text>
          </Post.Paragraph>
          {isLoading ? (
            <View style={{ padding: 20, alignItems: 'center' }}>
              <ActivityIndicator size="small" color={adaptive.blue500} />
            </View>
          ) : searchResults.length === 0 ? (
            <View style={{ padding: 20, alignItems: 'center' }}>
              <Paragraph.Text color={adaptive.grey500}>
                검색 결과가 없습니다.
              </Paragraph.Text>
            </View>
          ) : (
            <List rowSeparator="none">
              {searchResults.map((user) => {
                const isSelected = selectedUsers.find(u => u.userId === user.userId);
                return (
                  <Pressable
                    key={user.userId}
                    onPress={() => handleAddUser(user)}
                    disabled={!!isSelected}
                  >
                    <ListRow
                      contents={
                        <ListRow.Texts
                          type="2RowTypeB"
                          top={user.name}
                          topProps={{ color: adaptive.grey800 }}
                          bottom={user.email}
                          bottomProps={{ color: adaptive.grey600, typography: 't7' }}
                        />
                      }
                      verticalPadding="small"
                      right={
                        isSelected ? (
                          <Icon
                            name="icon-check-mono"
                            color={adaptive.blue500}
                            size={24}
                          />
                        ) : (
                          <Icon
                            name="icon-plus-mono"
                            color={adaptive.grey400}
                            size={24}
                          />
                        )
                      }
                    />
                  </Pressable>
                );
              })}
            </List>
          )}
          <Spacing size={20} />
        </>
      )}

      {/* 추가한 친구 */}
      <Post.Paragraph
        paddingBottom={8}
        typography="t7"
        color={adaptive.grey600}
      >
        <Paragraph.Text>추가한 친구 ({selectedUsers.length})</Paragraph.Text>
      </Post.Paragraph>
      {selectedUsers.length === 0 ? (
        <View style={{ padding: 20, alignItems: 'center' }}>
          <Paragraph.Text color={adaptive.grey500}>
            추가된 친구가 없습니다.
          </Paragraph.Text>
        </View>
      ) : (
        <List rowSeparator="none">
          {selectedUsers.map((user) => (
            <ListRow
              key={user.userId}
              contents={
                <ListRow.Texts
                  type="2RowTypeB"
                  top={user.name}
                  topProps={{ color: adaptive.grey700 }}
                  bottom={user.email}
                  bottomProps={{ color: adaptive.grey600, typography: 't7' }}
                />
              }
              verticalPadding="small"
              right={
                <Pressable onPress={() => handleRemoveUser(user.userId)}>
                  <Icon name="icon-chip-x-mono" color={adaptive.grey300} size={24} />
                </Pressable>
              }
            />
          ))}
        </List>
      )}

      <FixedBottomCTAProvider>
        <FixedBottomCTA.Double
          leftButton={
            <Button
              type="dark"
              style="weak"
              display="block"
              disabled={false}
              loading={false}
              onPress={() => navigation.navigate('/create-goal/step8')}
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
              onPress={handleNext}
            >
              다음
            </Button>
          }
        />
      </FixedBottomCTAProvider>
    </>
  );
}
