package com.mentoree.mentoring.dto;

import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.entity.ProgramRole;
import lombok.*;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ApplyRequestDto implements Serializable {

    private Long memberId;

    @NotNull
    private String nickname;

    @NotNull
    private Long programId;

    @NotNull
    private String message;

    @NotNull
    private String role;

    @Builder
    public ApplyRequestDto(Long memberId, String nickname, Long programId, String message, String role) {
        this.memberId = memberId;
        this.nickname = nickname;
        this.programId = programId;
        this.message = message;
        this.role = role;
    }

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

    public static ApplyRequestDto of(Participant participant) {
        return ApplyRequestDto.builder()
                .memberId(participant.getMemberId())
                .nickname(participant.getNickname())
                .message(participant.getMessage())
                .programId(participant.getProgram().getId())
                .role(participant.getRole().getValue())
                .build();
    }


}
