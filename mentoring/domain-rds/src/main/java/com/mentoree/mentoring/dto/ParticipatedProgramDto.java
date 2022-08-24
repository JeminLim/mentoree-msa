package com.mentoree.mentoring.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Getter
@Setter
@NoArgsConstructor
public class ParticipatedProgramDto implements Serializable {

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
