package com.mentoree.reply.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.reply.api.controller.ReplyApiController;
import com.mentoree.reply.domain.dto.ReplyDto;
import com.mentoree.reply.domain.entity.Reply;
import com.mentoree.reply.domain.service.ReplyService;
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
@WebMvcTest(controllers = ReplyApiController.class)
public class ReplyApiControllerTest {

    @MockBean
    ReplyService replyService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    private Reply replyA;
    private Reply replyB;
    private List<ReplyDto> replyList = new ArrayList<>();

    @BeforeEach
    void setUp() {

        replyA = Reply.builder()
                .nickname("testA")
                .memberId(1L)
                .boardId(1L)
                .content("test reply")
                .build();

        replyB = Reply.builder()
                .nickname("testB")
                .memberId(1L)
                .boardId(1L)
                .content("test reply B")
                .build();

        ReplyDto dtoA = ReplyDto.builder()
                .replyId(1L)
                .boardId(replyA.getBoardId())
                .content(replyA.getContent())
                .writerId(replyA.getMemberId())
                .writerNickname(replyA.getNickname())
                .build();

        ReplyDto dtoB = ReplyDto.builder()
                .replyId(2L)
                .boardId(replyB.getBoardId())
                .content(replyB.getContent())
                .writerId(replyB.getMemberId())
                .writerNickname(replyB.getNickname())
                .build();

        replyList.add(dtoA);
        replyList.add(dtoB);
    }

    @Test
    @WithMockUser
    @DisplayName("댓글_리스트_요청_테스트")
    void 댓글_리스트_요청_테스트() throws Exception {
        //given
        when(replyService.getReplies(any())).thenReturn(replyList);
        //when
        ResultActions result = mockMvc.perform(
                get("/api/replies/list")
                        .param("boardId", "1")
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.replyList.size()").value(2))
                .andExpect(jsonPath("$.replyList[0].replyId").value(1))
                .andExpect(jsonPath("$.replyList[1].replyId").value(2));
    }

    @Test
    @WithMockUser
    @DisplayName("댓글_작성_요청_테스트")
    void 댓글_작성_요청_테스트() throws Exception {
        //given
        ReplyDto requestForm = ReplyDto.builder()
                .writerNickname("test")
                .boardId(1L)
                .content("test request")
                .writerId(1L)
                .build();
        String requestBody = objectMapper.writeValueAsString(requestForm);
        //when
        ResultActions result = mockMvc.perform(
                post("/api/replies/new")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf())
        );

        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

}
