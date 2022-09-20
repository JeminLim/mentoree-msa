package com.mentoree.mentoring.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.mentoree.MentoringApiApplication;
import com.mentoree.common.interenal.ResponseMember;
import com.mentoree.mentoring.domain.entity.Board;
import com.mentoree.mentoring.domain.entity.Mission;
import com.mentoree.mentoring.domain.repository.BoardRepository;
import com.mentoree.mentoring.domain.repository.MissionRepository;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.domain.repository.ProgramRepository;
import com.mentoree.mentoring.dto.BoardInfoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MentoringApiApplication.class, properties = "spring.cloud.config.enabled=false")
@ActiveProfiles("test")
@AutoConfigureWireMock
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092"}, ports = { 9092 })
@ExtendWith({RestDocumentationExtension.class, SpringExtension.class})
@Transactional
public class BoardApiTest {

    @Autowired
    WireMockServer mockServer;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ProgramRepository programRepository;
    @Autowired
    ParticipantRepository participantRepository;
    @Autowired
    MissionRepository missionRepository;
    @Autowired
    BoardRepository boardRepository;
    DataPreparation dataPreparation;

    Map<String, Object> data = new HashMap<>();

    @BeforeEach
    void setUp() throws JsonProcessingException {
        dataPreparation =
                new DataPreparation(programRepository, participantRepository, missionRepository, boardRepository);
        data = dataPreparation.getData();
        ResponseMember clientResponse = new ResponseMember(3L, "testNick", Arrays.asList("LIFE"));
        String internalResponse = objectMapper.writeValueAsString(clientResponse);
        mockServer.stubFor(WireMock.get(WireMock.urlMatching("/api/members/internal/[1-9]"))
                .willReturn(WireMock.aResponse().withBody(internalResponse)
                        .withStatus(HttpStatus.OK.value())
                        .withHeader("Content-type", MediaType.APPLICATION_JSON_VALUE))
        );
    }

    @Test
    @WithMockUser(username = "testA@email.com", password="", roles = "USER")
    @DisplayName("수행보드_정보_요청")
    void 수행보드_정보_요청() throws Exception {

        Mission mission = (Mission) data.get("missionA");
        Board board = (Board) data.get("boardA");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/boards/{boardId}", board.getId())
                                .header("X-Authorization-Id", "1")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.boardInfo.boardId").value(board.getId()))
                .andExpect(jsonPath("$.boardInfo.missionId").value(mission.getId()))
                .andExpect(jsonPath("$.boardInfo.missionTitle").value(mission.getTitle()))
                .andExpect(jsonPath("$.boardInfo.writerId").value(board.getMemberId()))
                .andExpect(jsonPath("$.boardInfo.writerNickname").value(board.getNickname()))
                .andExpect(jsonPath("$.boardInfo.content").value(board.getContent()))
                .andDo(
                        document("/get/api-boards--boardId",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("boardId").description("Board pk wants to get")
                                ), responseFields(
                                        fieldWithPath("boardInfo.boardId").description("Board pk"),
                                        fieldWithPath("boardInfo.missionId").description("mission pk which the board is belong to"),
                                        fieldWithPath("boardInfo.missionTitle").description("mission title which the board is belong to"),
                                        fieldWithPath("boardInfo.writerId").description("Member pk who wrote the board"),
                                        fieldWithPath("boardInfo.writerNickname").description("Member nickname who wrote the board"),
                                        fieldWithPath("boardInfo.content").description("Board content")
                                )
                        )
                );

    }

    @Test
    @WithMockUser(username = "testA@email.com", password="", roles = "USER")
    @DisplayName("수행보드_생성_요청")
    void 수행보드_생성_요청() throws Exception {

        Mission mission = (Mission) data.get("missionA");
        BoardInfoDto requestForm = BoardInfoDto.builder()
                .content("new Board")
                .missionId(mission.getId())
                .missionTitle(mission.getTitle())
                .writerId(1L)
                .writerNickname("testerNick")
                .build();
        String requestBody = objectMapper.writeValueAsString(requestForm);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/boards/new")
                                .header("X-Authorization-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andDo(
                        document("/post/api-boards-new",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("boardId").ignored(),
                                        fieldWithPath("missionId").description("Mission goal"),
                                        fieldWithPath("missionTitle").description("Mission title"),
                                        fieldWithPath("writerId").description("Writer member pk"),
                                        fieldWithPath("writerNickname").description("Writer member nickname"),
                                        fieldWithPath("content").description("Board content")
                                ), responseFields(
                                        fieldWithPath("result").description("Result of request")
                                )
                        )
                );


    }
}
