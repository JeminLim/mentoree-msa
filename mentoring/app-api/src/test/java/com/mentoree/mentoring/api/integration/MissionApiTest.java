package com.mentoree.mentoring.api.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.mentoree.common.interenal.ResponseMember;
import com.mentoree.mentoring.domain.entity.Board;
import com.mentoree.mentoring.domain.entity.Mission;
import com.mentoree.mentoring.dto.MissionInfoDto;
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
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWireMock
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@ExtendWith(RestDocumentationExtension.class)
@Transactional
public class MissionApiTest {

    @Autowired
    WireMockServer mockServer;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    DataPreparation dataPreparation;

    Map<String, Object> data = new HashMap<>();

    @BeforeEach
    void setUp() throws JsonProcessingException {
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
    @DisplayName("미션_리스트_요청")
    void 미션_리스트_요청() throws Exception {

        Mission curMission = (Mission) data.get("missionA");

        mockMvc.perform(
                get("/api/missions/list")
                        .param("programId", "1")
                        .param("isOpen", "true")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.missions.size()").value(1))
                .andExpect(jsonPath("$.missions[0].missionId").value(curMission.getId()))
                .andExpect(jsonPath("$.missions[0].missionTitle").value(curMission.getTitle()))
                .andExpect(jsonPath("$.missions[0].missionGoal").value(curMission.getGoal()))
                .andExpect(jsonPath("$.missions[0].content").value(curMission.getContent()))
                .andExpect(jsonPath("$.missions[0].dueDate").value(curMission.getDueDate().toString()))
                .andDo(
                        document("/get/api/missions/list",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters (
                                        parameterWithName("programId").description("Program pk to load missions"),
                                        parameterWithName("isOpen").description("Whether mission is able to conduct then true, otherwise false")
                                ), responseFields(
                                        fieldWithPath("missions[]").description("Mission list"),
                                        fieldWithPath("missions[].missionId").description("Mission pk"),
                                        fieldWithPath("missions[].missionTitle").description("Mission title"),
                                        fieldWithPath("missions[].missionGoal").description("Mission goal"),
                                        fieldWithPath("missions[].content").description("Mission content"),
                                        fieldWithPath("missions[].dueDate").description("Mission due date")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "testA@email.com", password="", roles = "USER")
    @DisplayName("미션_상세정보_요청")
    void 미션_상세정보_요청() throws Exception {

        Mission curMission = (Mission) data.get("missionA");
        Board board = (Board) data.get("boardA");

        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/missions/{missionId}", curMission.getId())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.mission.missionId").value(curMission.getId()))
                .andExpect(jsonPath("$.mission.missionTitle").value(curMission.getTitle()))
                .andExpect(jsonPath("$.mission.missionGoal").value(curMission.getGoal()))
                .andExpect(jsonPath("$.mission.content").value(curMission.getContent()))
                .andExpect(jsonPath("$.mission.dueDate").value(curMission.getDueDate().toString()))
                .andExpect(jsonPath("$.boardList.size()").value(1))
                .andExpect(jsonPath("$.boardList[0].boardId").value(board.getId()))
                .andDo(
                        document("/get/api/missions/missionId",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("missionId").description("Mission pk want to get")
                                ), responseFields(
                                        fieldWithPath("mission").description("Mission list"),
                                        fieldWithPath("mission.missionId").description("Mission pk"),
                                        fieldWithPath("mission.missionTitle").description("Mission title"),
                                        fieldWithPath("mission.missionGoal").description("Mission goal"),
                                        fieldWithPath("mission.content").description("Mission content"),
                                        fieldWithPath("mission.dueDate").description("Mission due date"),
                                        fieldWithPath("boardList[]").description("Board list belong to current mission"),
                                        fieldWithPath("boardList[].boardId").description("Board pk"),
                                        fieldWithPath("boardList[].missionId").description("mission pk which the board is belong to"),
                                        fieldWithPath("boardList[].missionTitle").description("mission title which the board is belong to"),
                                        fieldWithPath("boardList[].writerId").description("Member pk who wrote the board"),
                                        fieldWithPath("boardList[].content").description("Board content")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "testA@email.com", password="", roles = "USER")
    @DisplayName("미션_생성_요청")
    void 미션_생성_요청() throws Exception {

        MissionInfoDto createRequestForm = MissionInfoDto.builder()
                .missionTitle("newMission")
                .missionGoal("new mission goal")
                .content("new mission")
                .dueDate(LocalDate.now().plusDays(3))
                .build();
        String requestBody = objectMapper.writeValueAsString(createRequestForm);

        mockMvc.perform(
                    RestDocumentationRequestBuilders.post("/api/missions/new")
                                .param("programId", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andDo(
                        document("/post/api/missions/new",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters (
                                        parameterWithName("programId").description("Program pk to load missions"),
                                        parameterWithName("_csrf").ignored()
                                ), requestFields(
                                        fieldWithPath("missionId").ignored(),
                                        fieldWithPath("missionTitle").description("Mission title"),
                                        fieldWithPath("missionGoal").description("Mission goal"),
                                        fieldWithPath("content").description("Mission content"),
                                        fieldWithPath("dueDate").description("Mission due date")
                                ), responseFields(
                                        fieldWithPath("result").description("Result of request")
                                )
                        )
                );

    }


}
