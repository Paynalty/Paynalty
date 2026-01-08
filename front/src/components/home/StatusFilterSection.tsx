import { ListHeader, Icon, Txt } from '@toss/tds-react-native';
import { useAdaptive } from '@toss/tds-react-native/private';
import React, { useState } from 'react';
import { Pressable, View, StyleSheet } from 'react-native';

type Status = 'ACTIVE' | 'PENDING' | 'COMPLETE';

interface Props {
  currentStatus: Status;
  onStatusChange: (status: Status) => void;
}

export function StatusFilterSection({ currentStatus, onStatusChange }: Props) {
  const adaptive = useAdaptive();
  const [showDropdown, setShowDropdown] = useState(false);

  const statusLabels: Record<Status, string> = {
    ACTIVE: '진행중인 챌린지',
    PENDING: '예정된 챌린지',
    COMPLETE: '완료된 챌린지',
  };

  return (
    <>
      <ListHeader
        title={
          <ListHeader.TitleSelector
            typography="t4"
            color={adaptive.grey800}
            fontWeight="bold"
            onPress={() => setShowDropdown(!showDropdown)}
          >
            {statusLabels[currentStatus]}
          </ListHeader.TitleSelector>
        }
      />
      {showDropdown && (
        <View style={styles.dropdownMenu}>
          {(['PENDING', 'ACTIVE', 'COMPLETE'] as Status[]).map((status) => (
            <Pressable
              key={status}
              style={styles.dropdownItem}
              onPress={() => {
                onStatusChange(status);
                setShowDropdown(false);
              }}
            >
              <Txt typography="t5" color={currentStatus === status ? adaptive.blue500 : adaptive.grey800}>
                {statusLabels[status]}
              </Txt>
              {currentStatus === status && <Icon name="icon-check-mono" color={adaptive.blue500} size={16} />}
            </Pressable>
          ))}
        </View>
      )}
    </>
  );
}

const styles = StyleSheet.create({
  dropdownMenu: {
    backgroundColor: 'white',
    borderRadius: 12,
    borderWidth: 1,
    borderColor: '#e5e7eb',
    marginHorizontal: 16,
    marginTop: 8,
    marginBottom: 12,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 8,
    elevation: 4,
  },
  dropdownItem: {
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    padding: 16,
  },
});
