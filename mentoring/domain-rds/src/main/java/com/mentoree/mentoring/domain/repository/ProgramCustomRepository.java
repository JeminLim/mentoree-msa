package com.mentoree.mentoring.domain.repository;

import com.mentoree.common.domain.Category;
import com.mentoree.mentoring.dto.ProgramInfoDto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Optional;

public interface ProgramCustomRepository {

    Slice<ProgramInfoDto> findAllProgram(Pageable pageable, List<Long> programIds);
    Slice<ProgramInfoDto> findRecommendProgram(Pageable pageable, List<Long> programIds, List<Category> interests);
    Optional<ProgramInfoDto> findProgramInfoById(Long programId);

}
