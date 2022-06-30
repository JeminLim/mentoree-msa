package com.mentoree.mentoring.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.common.domain.Category;
import com.mentoree.common.interenal.ResponseMember;
import com.mentoree.mentoring.api.controller.ProgramApiController;
import com.mentoree.mentoring.client.MemberClient;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.dto.*;
import com.mentoree.mentoring.service.ProgramService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = ProgramApiController.class)
public class ProgramApiControllerTest {

    @MockBean
    ProgramService programService;
    @MockBean
    MemberClient memberClient;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    ProgramInfoDto dtoA;
    ProgramInfoDto dtoB;
    Program programA;
    Program programB;
    List<ProgramInfoDto> programList = new ArrayList<>();
    ResponseMember responseMember;
    @BeforeEach
    void setUp() {
        dtoA = ProgramInfoDto.builder()
                .title("programA")
                .category("IT")
                .description("programA desc")
                .dueDate(LocalDate.now().plusDays(5))
                .maxMember(5)
                .goal("programA goal")
                .build();
        dtoB = ProgramInfoDto.builder()
                .title("programB")
                .category("ART")
                .description("programB desc")
                .dueDate(LocalDate.now().plusDays(3))
                .goal("programB goal")
                .maxMember(7)
                .build();

        programList.add(dtoA);
        programList.add(dtoB);

        programA = dtoA.toEntity();
        programB = dtoB.toEntity();

        responseMember = new ResponseMember();
        responseMember.setMemberId(1L);
        responseMember.setInterests(new ArrayList<>());
        responseMember.setNickname("testNick");

    }

    @Test
    @WithMockUser
    @DisplayName("프로그램_생성_테스트")
    void 프로그램_생성_테스트() throws Exception {
        //given
        ProgramCreateDto createForm = ProgramCreateDto.builder()
                .maxMember(3)
                .mentor(false)
                .goal("create test")
                .title("createTest")
                .description("for test")
                .build();
        String requestBody = objectMapper.writeValueAsString(createForm);
        ParticipatedProgramDto expectedReturn = ParticipatedProgramDto.builder()
                .id(1L)
                .title(createForm.getTitle())
                .build();
        when(programService.createProgram(any(ProgramCreateDto.class)))
                .thenReturn(expectedReturn);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/programs/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value(createForm.getTitle()));
    }

    @Test
    @DisplayName("프로그램_리스트_요청_테스트")
    @WithMockUser
    void 프로그램_리스트_요청_테스트() throws Exception {
        //given
        when(memberClient.getMember(any(Long.class))).thenReturn(responseMember);
        when(programService.getProgramList(any(Integer.class), any(Long.class))).thenReturn(programList);
        //when
        ResultActions result = mockMvc.perform(
                get("/api/programs/list")
                        .param("page", "0")
                        .param("memberId", "1")
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.program.size()").value(2));
    }

    @Test
    @WithMockUser
    @DisplayName("프로그램_추천_리스트_요청_테스트")
    void 프로그램_추천_리스트_요청_테스트() throws Exception {
        //given
        when(memberClient.getMember(any(Long.class))).thenReturn(responseMember);
        when(programService.getRecommendProgramList(any(Integer.class), any(Long.class),any())).thenReturn(programList);
        //when
        ResultActions result = mockMvc.perform(
                get("/api/programs/list/recommend")
                        .param("page", "0")
                        .param("memberId", "1")
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.program.size()").value(2));
    }

    @Test
    @WithMockUser
    @DisplayName("프로그램_상세정보_요청_테스트")
    void 프로그램_상세정보_요청_테스트() throws Exception {
        //given
        when(programService.getProgramInfo(any(Long.class))).thenReturn(dtoA);
        //when
        ResultActions result = mockMvc.perform(
                get("/api/programs/1")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("programA"));
    }

    @Test
    @WithMockUser
    @DisplayName("프로그램_참가_신청_테스트")
    void 프로그램_참가_신청_테스트() throws Exception {
        //given
        ApplyRequestDto applyRequest
                = new ApplyRequestDto(1L,
                "testNick", 1L,
                "apply msg", "MENTOR");
        String requestBody = objectMapper.writeValueAsString(applyRequest);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/programs/1/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(content().string("success"));
    }


    @Test
    @WithMockUser
    @DisplayName("참가자_관리_리스트_테스트")
    void 참가자_관리_리스트_테스트() throws Exception {
        //given
        ApplyRequestDto applyRequest
                = new ApplyRequestDto(1L,
                "testNick", 1L,
                "apply msg", "MENTOR");
        List<ApplyRequestDto> applicants = new ArrayList<>();
        applicants.add(applyRequest);

        when(programService.getProgramInfo(any(Long.class))).thenReturn(dtoA);
        when(programService.getApplicants(any(Long.class))).thenReturn(applicants);
        when(programService.countCurrentMember(any(Long.class))).thenReturn(1L);
        when(programService.isHost(any(Long.class), any(Long.class))).thenReturn(true);
        //when
        ResultActions result = mockMvc.perform(
                get("/api/programs/1/applicants")
                        .param("memberId", String.valueOf(1))
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.programInfo.title").value("programA"))
                .andExpect(jsonPath("$.applicants.[0].nickname").value("testNick"))
                .andExpect(jsonPath("$.curNum").value(1));
    }

    @Test
    @WithMockUser
    @DisplayName("참가자_승인_테스트")
    void 참가자_승인_테스트() throws Exception {
        //given
        ApplyRequestDto applyRequest
                = new ApplyRequestDto(1L,
                "testNick", 1L,
                "apply msg", "MENTOR");
        String requestBody = objectMapper.writeValueAsString(applyRequest);
        when(programService.isHost(any(Long.class), any(Long.class))).thenReturn(true);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/programs/1/applicants/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

    @Test
    @WithMockUser
    @DisplayName("참가자_거절_테스트")
    void 참가자_거절_테스트() throws Exception {
        //given
        ApplyRequestDto applyRequest
                = new ApplyRequestDto(1L,
                "testNick", 1L,
                "apply msg", "MENTOR");
        String requestBody = objectMapper.writeValueAsString(applyRequest);
        when(programService.isHost(any(Long.class), any(Long.class))).thenReturn(true);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/programs/1/applicants/reject")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

}
