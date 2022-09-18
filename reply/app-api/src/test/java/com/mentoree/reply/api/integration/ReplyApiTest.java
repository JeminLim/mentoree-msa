package com.mentoree.reply.api.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.reply.domain.dto.ReplyDto;
import com.mentoree.reply.domain.entity.Reply;
import com.mentoree.reply.domain.repository.ReplyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Transactional
public class ReplyApiTest {

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ReplyRepository replyRepository;

    private Reply replyA;
    private Reply replyB;

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

        replyA = replyRepository.save(replyA);
        replyB = replyRepository.save(replyB);
    }

    @Test
    @WithMockUser(username = "testA@email.com", password="", roles = "USER")
    @DisplayName("댓글_리스트_요청")
    void 댓글_리스트_요청() throws Exception {

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/replies/list")
                                .param("boardId", "1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.replyList.size()").value(2))
                .andDo(
                        document("/get/api-replies-list",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters (
                                        parameterWithName("boardId").description("Board pk for getting reply list")
                                ), responseFields(
                                        fieldWithPath("replyList").description("Reply list belonging to board"),
                                        fieldWithPath("replyList[].replyId").description("Reply pk"),
                                        fieldWithPath("replyList[].boardId").description("The board belonged"),
                                        fieldWithPath("replyList[].writerId").description("Reply writer member pk"),
                                        fieldWithPath("replyList[].writerNickname").description("Reply writer member nickname"),
                                        fieldWithPath("replyList[].content").description("Reply content"),
                                        fieldWithPath("replyList[].modifiedDate").description("Reply modified time")
                                )
                ));
    }

    @Test
    @WithMockUser(username = "testA@email.com", password="", roles = "USER")
    @DisplayName("댓글_작성_테스트")
    void 댓글_작성_테스트() throws Exception {

        ReplyDto requestForm = ReplyDto.builder()
                .writerId(1L)
                .writerNickname("testNick")
                .boardId(1L)
                .content("new test reply")
                .build();
        String requestBody = objectMapper.writeValueAsString(requestForm);

        mockMvc.perform(
                        post("/api/replies/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andDo(
                        document("/post/api-replies-new",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields (
                                        fieldWithPath("replyId").ignored(),
                                        fieldWithPath("boardId").description("The board belonged"),
                                        fieldWithPath("writerId").description("Reply writer member pk"),
                                        fieldWithPath("writerNickname").description("Reply writer member nickname"),
                                        fieldWithPath("content").description("Reply content"),
                                        fieldWithPath("modifiedDate").description("Reply modified time")
                                ), responseFields(
                                        fieldWithPath("result").description("Result of request"),
                                        fieldWithPath("reply.replyId").description("Reply Id"),
                                        fieldWithPath("reply.boardId").description("The board belonged"),
                                        fieldWithPath("reply.writerId").description("Reply writer member pk"),
                                        fieldWithPath("reply.writerNickname").description("Reply writer member nickname"),
                                        fieldWithPath("reply.content").description("Reply content"),
                                        fieldWithPath("reply.modifiedDate").description("Reply modified time")
                                )
                        )
                );


    }


}
