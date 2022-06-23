package com.mentoree.mentoring.service;

import com.mentoree.common.domain.Category;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.entity.ProgramRole;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.domain.repository.ProgramRepository;
import com.mentoree.mentoring.dto.ApplyRequestDto;
import com.mentoree.mentoring.dto.ParticipatedProgramDto;
import com.mentoree.mentoring.dto.ProgramCreateDto;
import com.mentoree.mentoring.messagequeue.connect.ConnectProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgramServiceTest {

    @Mock
    private ProgramRepository programRepository;
    @Mock
    private ParticipantRepository participantRepository;

    @Mock
    private ConnectProducer<Program> programConnectProducer;
    @Mock
    private ConnectProducer<Participant> participantConnectProducer;

    @InjectMocks
    private ProgramService programService;

    private Program program;
    private Participant participant;
    private Participant applicant;

    @BeforeEach
    public void setUp() {
        //given
        program = Program.builder()
                .programName("testProgram")
                .category(Category.IT)
                .dueDate(LocalDate.now().plusDays(8))
                .description("test program")
                .goal("for test")
                .maxMember(5)
                .build();
        participant = Participant.builder()
                .program(program)
                .approval(true)
                .isHost(true)
                .memberId(1L)
                .role(ProgramRole.valueOf("MENTOR"))
                .build();
        applicant = Participant.builder()
                .program(program)
                .approval(false)
                .isHost(false)
                .memberId(2L)
                .role(ProgramRole.valueOf("MENTEE"))
                .message("apply")
                .build();
    }

    @Test
    @DisplayName("프로그램_생성_테스트")
    void 프로그램_생성_테스트() {
        //given
        ProgramCreateDto createRequest = ProgramCreateDto.builder()
                .programName("testProgram")
                .category("IT")
                .dueDate(LocalDate.now().plusDays(8))
                .description("test program")
                .goal("for test")
                .programRole("MENTOR")
                .mentor(false)
                .targetNumber(5)
                .build();
        createRequest.setMemberId(1L);
        when(programRepository.save(any(Program.class))).thenReturn(program);
        when(participantRepository.save(any(Participant.class))).thenReturn(participant);

        //when
        ParticipatedProgramDto result = programService.createProgram(createRequest);

        //then
        assertThat(result.getId()).isEqualTo(program.getId());
        assertThat(result.getTitle()).isEqualTo(program.getProgramName());
    }

    @Test
    @DisplayName("프로그램_지원_성공_테스트")
    void 프로그램_지원_성공_테스트() {
        //given
        ApplyRequestDto applyRequest = new ApplyRequestDto(1L, "testNick", 1L, "apply msg", "MENTOR");
        when(participantRepository.isApplicant(any(Long.class), any(Long.class))).thenReturn(0L);
        when(programRepository.findById(any(Long.class))).thenReturn(Optional.of(program));
        //when
        programService.applyProgram(applyRequest);
        //then
        verify(programRepository).findById(1L);
        verify(participantRepository).save(any());
    }

    @Test
    @DisplayName("프로그램_지원_실패_테스트")
    void 프로그램_지원_실패_테스트() {
        //given
        ApplyRequestDto applyRequest = new ApplyRequestDto(1L, "testNick", 1L, "apply msg", "MENTOR");
        when(participantRepository.isApplicant(any(Long.class), any(Long.class))).thenReturn(1L);
        //when
        assertThatThrownBy(() -> {
            programService.applyProgram(applyRequest);
        }).isInstanceOf(RuntimeException.class);
    }

    @Test
    @DisplayName("프로그램_지원자_승인_테스트")
    void 프로그램_지원자_승인_테스트() {
        //given
        when(participantRepository.findParticipantByProgramIdAndMemberId(any(Long.class), any(Long.class)))
                .thenReturn(Optional.of(applicant));
        //when
        programService.approval(1L, 2L);
        //then
        assertThat(applicant.isApproval()).isEqualTo(true);
    }

}
