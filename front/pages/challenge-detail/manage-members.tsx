import { createRoute, useNavigation } from '@granite-js/react-native';
import { useChallengeStore } from '../../src/stores/challengeStore';
import { useMutation, useQueryClient } from '@tanstack/react-query';
import { updateChallengeMembers } from '../../src/api/challengeMembers';
import { getChallengeDetail } from '../../src/api/challenges';
import { MemberManager } from '../../src/components/challenge/MemberManager';
import { Alert } from 'react-native';
import { UserResponse } from '../../src/api/users';
import { useMe } from '../../src/hooks/useMe';

export const Route = createRoute('/challenge-detail/manage-members', {
  component: Page,
});

export default function Page() {
  const navigation = useNavigation();
  const queryClient = useQueryClient();
  const selectedChallenge = useChallengeStore((s) => s.selectedChallengeObject);
  const { data: me } = useMe();

  const members = selectedChallenge?.members || [];
  
  // ChallengeMemberResponse 형태를 UserResponse 형태로 변환 (이메일은 없으므로 빈 문자열 처리)
  const initialMembers: UserResponse[] = members.map(m => ({
    id: 0, // 관리용 ID (실제 업데이트는 tossId 사용)
    tossId: m.tossId,
    name: m.userName,
    email: '', // 상세 정보에 이메일이 없다면 빈 값 or API 추가 필요
  }));

  const { mutate, isPending } = useMutation({
    mutationFn: (tossIds: number[]) => {
      if (!selectedChallenge) throw new Error('챌린지 정보가 없습니다.');
      return updateChallengeMembers(selectedChallenge.id, tossIds);
    },
    onSuccess: () => {
      Alert.alert('성공', '멤버 정보를 수정했어요.', [
        {
          text: '확인',
          onPress: () => {
             // 챌린지 상세 데이터 갱신
             queryClient.invalidateQueries({ queryKey: ['myProgressChallenges'] });

             // 최신 데이터 가져와서 Store 업데이트 (단건 API 사용)
             if (selectedChallenge) {
                getChallengeDetail(selectedChallenge.id).then((freshData) => {
                   useChallengeStore.getState().setSelectedChallenge({
                     ...freshData,
                   });
                   navigation.pop();
                });
             } else {
                 navigation.pop();
             }
          },
        },
      ]);
    },
    onError: (error) => {
      console.error(error);
      Alert.alert('실패', '멤버 수정에 실패했어요.');
    },
  });

  if (!selectedChallenge) return null;

  return (
    <MemberManager
      initialMembers={initialMembers}
      disabledMemberIds={me ? [me.tossId] : []} // 나(생성자)는 삭제 불가
      onSave={(users) => {
        const tossIds = users.map(u => u.tossId);
        mutate(tossIds);
      }}
      saveButtonText="저장하기"
      isLoading={isPending}
    />
  );
}
