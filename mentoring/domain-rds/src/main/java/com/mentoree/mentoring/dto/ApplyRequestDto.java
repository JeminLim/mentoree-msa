package com.mentoree.mentoring.dto;

import com.mentoree.common.domain.DataTransferObject;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.entity.ProgramRole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ApplyRequestDto extends DataTransferObject {

    private Long memberId;

    @NotNull
    private String nickname;

    @NotNull
    private Long programId;

    @NotNull
    private String message;

    @NotNull
    private String role;

    public Participant toEntity(Program targetProgram) {
        return Participant.builder()
                .program(targetProgram)
                .isHost(false)
                .approval(false)
                .role(ProgramRole.valueOf(role))
                .message(message)
                .memberId(memberId)
                .nickname(nickname)
                .build();
    }


}
