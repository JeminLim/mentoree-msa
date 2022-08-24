package com.mentoree.mentoring.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.mentoring.api.controller.BoardApiController;
import com.mentoree.mentoring.dto.BoardInfoDto;
import com.mentoree.mentoring.service.BoardService;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest(controllers = BoardApiController.class)
public class BoardApiControllerTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @MockBean
    BoardService boardService;

    BoardInfoDto board;
    BoardInfoDto create;

    @BeforeEach
    void setUp() {
        board = BoardInfoDto.builder()
                .boardId(1L)
                .content("boardA")
                .missionId(1L)
                .missionTitle("missionA")
                .build();

        create = BoardInfoDto.builder()
                .content("new")
                .missionId(1L)
                .missionTitle("missionA")
                .writerId(1L)
                .writerNickname("testerNick")
                .build();
    }

    @Test
    @WithMockUser
    @DisplayName("수행보드_정보_가져오기_테스트")
    void 수행보드_정보_가져오기_테스트() throws Exception {
        //given
        when(boardService.getBoardInfo(any(Long.class))).thenReturn(board);
        //when
        ResultActions result = mockMvc.perform(
                get("/api/boards/1")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.boardInfo.boardId").value("1"));
    }

    @Test
    @WithMockUser
    @DisplayName("수행보드_등록_테스트")
    void 수행보드_등록_테스트() throws Exception {
        //given
        String requestBody = objectMapper.writeValueAsString(create);
        when(boardService.isParticipation(any(Long.class))).thenReturn(true);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/boards/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .param("memberId", String.valueOf(1))
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }


}
