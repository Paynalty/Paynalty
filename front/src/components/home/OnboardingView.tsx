import { Icon, Txt } from '@toss/tds-react-native';
import { Spacing } from '@granite-js/react-native';
import { LottieView } from '@granite-js/native/lottie-react-native';
import { ListHeader } from '@toss/tds-react-native';
import { Pressable, StyleSheet, View } from 'react-native';

interface OnboardingViewProps {
  adaptive: Record<string, string>;
  onCreateObjective: () => void;
  currentStatus: 'ACTIVE' | 'PENDING' | 'COMPLETE';
}

export function OnboardingView({ adaptive, onCreateObjective, currentStatus }: OnboardingViewProps) {
  const getMessage = () => {
    switch (currentStatus) {
      case 'ACTIVE':
        return {
          title: '아직 진행 중인 챌린지가 없어요',
          description: '작은 습관 하나가 큰 변화를 만들어요.\n지금 바로 첫 번째 목표를 세워볼까요?',
        };
      case 'PENDING':
        return {
          title: '예정된 챌린지가 없어요',
          description: '새로운 챌린지를 시작할 준비가 되셨나요?\n목표를 설정하고 시작해보세요!',
        };
      case 'COMPLETE':
        return {
          title: '완료된 챌린지가 없어요',
          description: '첫 번째 챌린지를 완료하고\n성취감을 느껴보세요!',
        };
    }
  };

  const message = getMessage();

  return (
    <View style={styles.onboardingContainer}>
      <View style={{ marginTop: -80, marginBottom: -60 }} pointerEvents="none">
        <LottieView
          source={{ uri: 'https://lottie.host/ab39ffda-09a1-44fe-ade6-1236d48e6720/AixS7quFKd.lottie' }}
          autoPlay
          loop
          renderMode="SOFTWARE"
          style={{ width: 200, height: 200 }}
        />
      </View>
      <Txt typography="t5" fontWeight="bold" color={adaptive.grey800} style={{ textAlign: 'center' }}>
        {message.title}
      </Txt>
      <Spacing size={8} />
      <Txt typography="t6" color={adaptive.grey600} style={{ textAlign: 'center' }}>
        {message.description}
      </Txt>
      <Spacing size={12} />
      <ListHeader
        title={<ListHeader.TitleParagraph color={adaptive.grey700}>이런 목표는 어때요?</ListHeader.TitleParagraph>}
      />
      <Spacing size={12} />
      <View style={styles.templateGrid}>
        {['매일 물 2L 마시기', '아침 8시 기상하기', '하루 30분 독서'].map((template) => (
          <Pressable key={template} style={styles.templateCard} onPress={onCreateObjective}>
            <Txt typography="t6" fontWeight="semibold" color={adaptive.grey800}>
              {template}
            </Txt>
            <Icon name="icon-arrow-right-small-mono" color={adaptive.grey400} size={16} />
          </Pressable>
        ))}
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  onboardingContainer: {
    paddingHorizontal: 24,
    alignItems: 'center',
    paddingVertical: 40,
  },
  templateGrid: {
    width: '100%',
    gap: 12,
    marginTop: 8,
  },
  templateCard: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
    backgroundColor: '#f9fafb',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#f2f4f6',
  },
});
