package com.mentoree.mentoring.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.mentoring.api.controller.MissionApiController;
import com.mentoree.mentoring.dto.BoardInfoDto;
import com.mentoree.mentoring.dto.MissionInfoDto;
import com.mentoree.mentoring.service.BoardService;
import com.mentoree.mentoring.service.MissionService;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = MissionApiController.class)
public class MissionApiControllerTest {

    @MockBean
    private MissionService missionService;
    @MockBean
    private BoardService boardService;
    @MockBean
    private ProgramService programService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    MissionInfoDto missionA;
    MissionInfoDto missionB;
    List<MissionInfoDto> missionList = new ArrayList<>();
    BoardInfoDto boardA;
    BoardInfoDto boardB;
    List<BoardInfoDto> boardList = new ArrayList<>();

    @BeforeEach
    void setUp() {
        missionA = MissionInfoDto.builder()
                .missionId(1L)
                .missionTitle("missionA")
                .missionGoal("missionA Goal")
                .content("missionA content")
                .dueDate(LocalDate.now().plusDays(2))
                .build();
        missionB = MissionInfoDto.builder()
                .missionId(2L)
                .missionTitle("missionB")
                .missionGoal("missionB Goal")
                .content("missionB content")
                .dueDate(LocalDate.now().plusDays(5))
                .build();
        missionList.add(missionA);
        missionList.add(missionB);
        boardA = BoardInfoDto.builder()
                .missionId(1L)
                .missionTitle("missionA")
                .boardId(1L)
                .content("boardA content")
                .writerId(1L)
                .build();
        boardB = BoardInfoDto.builder()
                .missionId(2L)
                .missionTitle("missionB")
                .boardId(2L)
                .content("boardB content")
                .writerId(1L)
                .build();
        boardList.add(boardA);
        boardList.add(boardB);
    }

    @Test
    @WithMockUser
    @DisplayName("미션_리스트_요청_테스트")
    void 미션_리스트_요청_테스트() throws Exception {
        //given
        when(missionService.getMissionList(any(Long.class), any(boolean.class)))
                .thenReturn(missionList);
        //when
        ResultActions result = mockMvc.perform(
                get("/api/missions/list")
                        .param("programId", String.valueOf(1L))
                        .param("isOpen", String.valueOf(true))
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.missions.size()").value(2))
                .andExpect(jsonPath("$.missions.[0].missionTitle").value("missionA"))
                .andExpect(jsonPath("$.missions.[1].missionTitle").value("missionB"));
    }

    @Test
    @WithMockUser
    @DisplayName("미션_상세정보_요청_테스트")
    void 미션_상세정보_요청_테스트() throws Exception {
        //given
        when(missionService.getMissionInfo(any(Long.class))).thenReturn(missionA);
        when(boardService.getBoardList(any(Long.class))).thenReturn(boardList);

        //when
        ResultActions result = mockMvc.perform(
                get("/api/missions/1")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.mission.missionTitle").value("missionA"))
                .andExpect(jsonPath("$.boardList.size()").value(2))
                .andExpect(jsonPath("$.boardList.[0].content").value("boardA content"))
                .andExpect(jsonPath("$.boardList.[1].content").value("boardB content"));
    }

    @Test
    @WithMockUser
    @DisplayName("미션_생성_테스트")
    void 미션_생성_테스트() throws Exception {
        //given
        String requestBody = objectMapper.writeValueAsString(missionA);
        when(programService.isHost(any(), any())).thenReturn(true);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/missions/new")
                        .header("X-Authorization-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .param("programId", String.valueOf(1))
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

}
