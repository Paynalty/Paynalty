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
import { Spacing } from '@granite-js/react-native';
import { useState, ReactNode } from 'react';
import { View, ActivityIndicator, Pressable, ScrollView, StyleSheet } from 'react-native';
import { useUserSearch } from '../../hooks/useUsers';
import { UserResponse } from '../../api/users';

interface MemberManagerProps {
  initialMembers?: UserResponse[];
  disabledMemberIds?: number[]; // tossId to disable removal
  onSave: (members: UserResponse[]) => void;
  /**
   * 멤버 목록이 변경될 때마다 호출됩니다.
   * 부모 컴포넌트에서 실시간으로 상태를 동기화해야 할 때 사용합니다.
   */
  onChange?: (members: UserResponse[]) => void;
  saveButtonText?: string;
  leftButton?: ReactNode;
  isLoading?: boolean;
}

export function MemberManager({
  initialMembers = [],
  disabledMemberIds = [],
  onSave,
  onChange,
  saveButtonText = '저장',
  leftButton,
  isLoading: isExternalLoading = false,
}: MemberManagerProps) {
  const adaptive = useAdaptive();
  const [searchText, setSearchText] = useState('');
  const [selectedUsers, setSelectedUsers] = useState<UserResponse[]>(initialMembers);

  // 사용자 검색 Hook
  const { data: searchResults = [], isLoading: isSearchLoading } = useUserSearch(searchText);

  const handleToggleUser = (user: UserResponse) => {
    // 이미 선택된 유저인지 확인
    const isSelected = selectedUsers.find((u) => u.tossId === user.tossId);
    
    // 비활성화된(삭제 불가능한) 멤버라면 토글 동작 안함 (선택 해제 불가)
    if (isSelected && disabledMemberIds.includes(user.tossId)) {
      return;
    }

    let newSelectedUsers;
    if (isSelected) {
      // 이미 선택됨 -> 제거
      newSelectedUsers = selectedUsers.filter((u) => u.tossId !== user.tossId);
    } else {
      // 선택 안됨 -> 추가
      newSelectedUsers = [...selectedUsers, user];
    }
    
    setSelectedUsers(newSelectedUsers);
    onChange?.(newSelectedUsers);
  };

  const handleRemoveUser = (tossId: number) => {
    if (disabledMemberIds.includes(tossId)) return;
    const newSelectedUsers = selectedUsers.filter((u) => u.tossId !== tossId);
    setSelectedUsers(newSelectedUsers);
    onChange?.(newSelectedUsers);
  };

  return (
    <FixedBottomCTAProvider>
      <ScrollView style={styles.container} contentContainerStyle={styles.content}>
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
          <View style={styles.section}>
            <Post.Paragraph paddingBottom={8} typography="t7" color={adaptive.grey600}>
              <Paragraph.Text>검색 결과</Paragraph.Text>
            </Post.Paragraph>
            {isSearchLoading ? (
              <View style={{ padding: 20, alignItems: 'center' }}>
                <ActivityIndicator size="small" color={adaptive.blue500} />
              </View>
            ) : searchResults.length === 0 ? (
              <View style={{ padding: 20, alignItems: 'center' }}>
                <Paragraph.Text color={adaptive.grey500}>검색 결과가 없습니다.</Paragraph.Text>
              </View>
            ) : (
              <List rowSeparator="none">
                {searchResults.map((user) => {
                  const isSelected = selectedUsers.some((u) => u.tossId === user.tossId);
                  
                  return (
                    <Pressable key={user.tossId} onPress={() => handleToggleUser(user)}>
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
                            <Icon name="icon-check-mono" color={adaptive.blue500} size={24} />
                          ) : (
                            <Icon name="icon-plus-mono" color={adaptive.grey400} size={24} />
                          )
                        }
                      />
                    </Pressable>
                  );
                })}
              </List>
            )}
            <Spacing size={20} />
          </View>
        )}

        {/* 추가한 친구 */}
        <View style={styles.section}>
          <Post.Paragraph paddingBottom={8} typography="t7" color={adaptive.grey600}>
            <Paragraph.Text>추가한 친구 ({selectedUsers.length})</Paragraph.Text>
          </Post.Paragraph>
          {selectedUsers.length === 0 ? (
            <View style={{ padding: 20, alignItems: 'center' }}>
              <Paragraph.Text color={adaptive.grey500}>추가된 친구가 없습니다.</Paragraph.Text>
            </View>
          ) : (
            <List rowSeparator="none">
              {selectedUsers.map((user) => {
                const isDisabled = disabledMemberIds.includes(user.tossId);
                return (
                  <ListRow
                    key={user.tossId}
                    contents={
                      <ListRow.Texts
                        type="2RowTypeB"
                        top={user.name}
                        topProps={{ color: adaptive.grey700 }}
                        bottom={user.email || ''} 
                        bottomProps={{ color: adaptive.grey600, typography: 't7' }}
                      />
                    }
                    verticalPadding="small"
                    right={
                      !isDisabled ? (
                        <Pressable onPress={() => handleRemoveUser(user.tossId)}>
                          <Icon name="icon-chip-x-mono" color={adaptive.grey300} size={24} />
                        </Pressable>
                      ) : undefined
                    }
                  />
                );
              })}
            </List>
          )}
        </View>
        <Spacing size={100} />
      </ScrollView>

      {leftButton ? (
        <FixedBottomCTA.Double
          leftButton={leftButton}
          rightButton={
            <Button
              type="primary"
              style="fill"
              display="block"
              disabled={isExternalLoading}
              loading={isExternalLoading}
              onPress={() => onSave(selectedUsers)}
            >
              {saveButtonText}
            </Button>
          }
        />
      ) : (
        <FixedBottomCTA
          disabled={isExternalLoading}
          loading={isExternalLoading}
          onPress={() => onSave(selectedUsers)}
        >
          {saveButtonText}
        </FixedBottomCTA>
      )}
    </FixedBottomCTAProvider>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: 'white',
  },
  content: {
    paddingBottom: 20,
  },
  section: {
    paddingHorizontal: 16,
  },
});
