package com.mentoree.mentoring.dto;

import com.mentoree.common.domain.DataTransferObject;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@NoArgsConstructor
public class ParticipatedProgramDto extends DataTransferObject {

    @NotNull
    private Long id;

    @NotNull
    private String title;

    @Builder
    public ParticipatedProgramDto(Long id, String title) {
        this.id = id;
        this.title = title;
    }
}
