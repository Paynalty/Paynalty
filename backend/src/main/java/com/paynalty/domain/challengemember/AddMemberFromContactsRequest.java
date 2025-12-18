package com.paynalty.domain.challengemember;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class AddMemberFromContactsRequest {

    @NotNull(message = "챌린지 ID는 필수입니다")
    private Long challengeId;

    @NotEmpty(message = "추가할 친구의 전화번호 목록은 필수입니다")
    private List<String> phoneNumbers;
}

