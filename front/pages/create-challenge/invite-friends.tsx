import { Button } from '@toss/tds-react-native';
import { createRoute } from '@granite-js/react-native';
import { useCreateChallengeStore } from '../../src/stores/createChallengeStore';
import { UserResponse } from '../../src/api/users';
import { MemberManager } from '../../src/components/challenge/MemberManager';

export const Route = createRoute('/create-challenge/invite-friends', {
  component: Page,
});

export default function Page() {
  const navigation = Route.useNavigation();
  const { data, updateData } = useCreateChallengeStore();
  const invitedUsers = data.invitedUsers || [];

  // 다음 단계로 이동 시 선택된 사용자 ID를 store에 저장
  const handleNext = (selectedUsers: UserResponse[]) => {
    updateData({ invitedUsers: selectedUsers });
    navigation.navigate('/create-challenge/complete');
  };

  return (
    <MemberManager
      initialMembers={invitedUsers}
      onSave={handleNext}
      onChange={(members) => updateData({ invitedUsers: members })} // 실시간 동기화
      saveButtonText="다음"
      leftButton={
        <Button
          type="dark"
          style="weak"
          display="block"
          onPress={() => {
            navigation.navigate('/create-challenge/step8');
          }}
        >
          이전
        </Button>
      }
    />
  );
}
