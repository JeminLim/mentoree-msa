package com.mentoree.mentoring.domain;

import com.mentoree.common.domain.Category;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.repository.ProgramRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

@DataJpaTest
public class ProgramRepositoryTest {

    @Autowired
    private ProgramRepository programRepository;

    private Program preProgram;

    @BeforeEach
    public void Set_Up_Data() {

        preProgram = Program.builder().title("testProgram")
                .category(Category.ART)
                .dueDate(LocalDate.now().plusDays(5))
                .description("test")
                .goal("testGoal")
                .maxMember(5)
                .build();
        programRepository.save(preProgram);
    }

    @Test
    @DisplayName("프로그램_생성_테스트_성공")
    public void 프로그램_생성_테스트_성공() {

        //given
        Program newProgram = Program.builder().title("newProgram")
                .category(Category.IT)
                .dueDate(LocalDate.now().plusDays(5))
                .description("testNewProgram")
                .goal("newGoal")
                .maxMember(5)
                .build();
        //when
        Program saved = programRepository.save(newProgram);

        Optional<Program> findProgram = programRepository.findById(saved.getId());

        //then
        assertThat(findProgram).isNotEmpty();
        assertThat(findProgram.get().getId()).isEqualTo(saved.getId());
    }

    @Test
    @DisplayName("프로그램_수정_테스트_성공")
    void 프로그램_수정_테스트_성공() {
        //given
        String changeCategory = Category.EMPLOYMENT.toString();
        String changeTitle = "changedName";
        String changeGoal = "changeGoal";
        int changeMaxMember = 2;
        String changeDescription = "changed Desc";
        LocalDate changeDueDate = LocalDate.now().plusDays(9);


        //when
        preProgram.changeCategory(changeCategory);
        preProgram.changeTitle(changeTitle);
        preProgram.changeGoal(changeGoal);
        preProgram.changeMaxMember(changeMaxMember);
        preProgram.changeDescription(changeDescription);
        preProgram.changeDueDate(changeDueDate);
        programRepository.save(preProgram);

        Optional<Program> afterChange = programRepository.findById(preProgram.getId());
        //then
        assertThat(afterChange).isNotEmpty();
        assertThat(afterChange.get().getCategory()).isEqualTo(Category.EMPLOYMENT);
        assertThat(afterChange.get().getTitle()).isEqualTo(changeTitle);
        assertThat(afterChange.get().getGoal()).isEqualTo(changeGoal);
        assertThat(afterChange.get().getMaxMember()).isEqualTo(changeMaxMember);
        assertThat(afterChange.get().getDescription()).isEqualTo(changeDescription);
        assertThat(afterChange.get().getDueDate()).isEqualTo(changeDueDate);
    }



}
