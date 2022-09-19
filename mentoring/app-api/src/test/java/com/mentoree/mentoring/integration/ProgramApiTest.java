package com.mentoree.mentoring.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.mentoree.MentoringApiApplication;
import com.mentoree.common.interenal.ResponseMember;
import com.mentoree.mentoring.client.MemberClient;
import com.mentoree.mentoring.domain.entity.Program;
import com.mentoree.mentoring.domain.repository.BoardRepository;
import com.mentoree.mentoring.domain.repository.MissionRepository;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import com.mentoree.mentoring.domain.repository.ProgramRepository;
import com.mentoree.mentoring.dto.ApplyRequestDto;
import com.mentoree.mentoring.dto.ProgramCreateDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = MentoringApiApplication.class, properties = "spring.cloud.config.enabled=false")
@AutoConfigureWireMock
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092"}, ports = { 9092 })
@ExtendWith(RestDocumentationExtension.class)
@Transactional
public class ProgramApiTest {

    @Autowired
    WireMockServer mockServer;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    MemberClient memberClient;

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
    @WithMockUser(username = "testA@email.com", password = "", roles = "USER")
    @DisplayName("프로그램_생성")
    void 프로그램_생성() throws Exception {

        ProgramCreateDto requestForm = ProgramCreateDto.builder()
                .description("forTestProgram")
                .title("integrationTestProgram")
                .goal("integrationTest")
                .dueDate(LocalDate.now().plusDays(5))
                .mentor(false)
                .category("MUSIC")
                .maxMember(3)
                .memberId(1L)
                .memberNickname("testNick")
                .build();

        String requestBody = objectMapper.writeValueAsString(requestForm);

        mockMvc.perform(
                        post("/api/programs/new")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.participatedProgram.id").exists())
                .andExpect(jsonPath("$.participatedProgram.title").value(requestForm.getTitle()))
                .andDo(
                        document("/post/api-programs-new",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestFields(
                                        fieldWithPath("title").description("Program title to create"),
                                        fieldWithPath("goal").description("Program goal"),
                                        fieldWithPath("description").description("Program description"),
                                        fieldWithPath("maxMember").description("Maximum member can be able to participate program"),
                                        fieldWithPath("category").description("Program's category"),
                                        fieldWithPath("dueDate").description("due date of recruitment"),
                                        fieldWithPath("memberId").description("Member pk who apply to create program"),
                                        fieldWithPath("memberNickname").description("Member's nickname"),
                                        fieldWithPath("mentor").description("Program role wants to participate")
                                ), responseFields(
                                        fieldWithPath("participatedProgram.id").description("Created program id"),
                                        fieldWithPath("participatedProgram.title").description("Created program title")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "testA@email.com", password = "", roles = "USER")
    @DisplayName("프로그램_리스트_요청")
    void 프로그램_리스트_요청() throws Exception {

        mockMvc.perform(
                        get("/api/programs/list")
                                .param("page", "0")
                                .param("memberId", "3")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.programList.size()").value(2))
                .andExpect(jsonPath("$.programList[0].title").value("testProgram"))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andDo(
                        document("/get/api-programs-list",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("page").description("Loaded page"),
                                        parameterWithName("memberId").description("Login member pk. If this field is absent, return all programs regardless of participation")
                                ), responseFields(
                                        fieldWithPath("programList").description("Program info list regardless of user's interest"),
                                        fieldWithPath("programList[].id").description("Program pk"),
                                        fieldWithPath("programList[].title").description("Program title to create"),
                                        fieldWithPath("programList[].goal").description("Program goal"),
                                        fieldWithPath("programList[].description").description("Program description"),
                                        fieldWithPath("programList[].maxMember").description("Maximum member can be able to participate program"),
                                        fieldWithPath("programList[].category").description("Program's category"),
                                        fieldWithPath("programList[].dueDate").description("due date of recruitment"),
                                        fieldWithPath("programList[].mentor[]").description("List of mentor"),
                                        fieldWithPath("hasNext").description("Whether next page is exist or not")
                                )
                        ));
    }

    @Test
    @WithMockUser(username = "testA@email.com", password = "", roles = "USER")
    @DisplayName("추천_프로그램_리스트_신청")
    void 추천_프로그램_리스트_신청() throws Exception {
        //given
        ResponseMember clientResponse = new ResponseMember(3L, "testNick", Arrays.asList("LIFE"));
        when(memberClient.getMemberInfo(any())).thenReturn(clientResponse);
        //when
        mockMvc.perform(
                        get("/api/programs/recommendations/list")
                                .header("X-Authorization-Id", "3")
                                .param("page", "0")
                                .param("memberId", "3")
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.recommendProgramList.size()").value(1))
                .andExpect(jsonPath("$.recommendProgramList[0].title").value("testProgramB"))
                .andExpect(jsonPath("$.hasNext").value(false))
                .andDo(
                        document("/get/api-programs-recommendations-list",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                        parameterWithName("page").description("Loaded page"),
                                        parameterWithName("memberId").description("Login member pk")
                                ), responseFields(
                                        fieldWithPath("recommendProgramList").description("Program info list regardless of user's interest"),
                                        fieldWithPath("recommendProgramList[].id").description("Program pk"),
                                        fieldWithPath("recommendProgramList[].title").description("Program title to create"),
                                        fieldWithPath("recommendProgramList[].goal").description("Program goal"),
                                        fieldWithPath("recommendProgramList[].description").description("Program description"),
                                        fieldWithPath("recommendProgramList[].maxMember").description("Maximum member can be able to participate program"),
                                        fieldWithPath("recommendProgramList[].category").description("Program's category"),
                                        fieldWithPath("recommendProgramList[].dueDate").description("due date of recruitment"),
                                        fieldWithPath("recommendProgramList[].mentor").description("List of mentor's info(pk, nickname)"),
                                        fieldWithPath("hasNext").description("Whether next page is exist or not")
                                )
                        ));
    }

    @Test
    @WithMockUser(username = "testA@email.com", password = "", roles = "USER")
    @DisplayName("프로그램_상세_정보_요청")
    void 프로그램_상세_정보_요청() throws Exception {

        Program program = (Program) data.get("programA");

        mockMvc.perform(
                        // PathVariable 을 사용할 때는 RestDocs 에 있는 get을 사용하자
                        RestDocumentationRequestBuilders.get("/api/programs/{programId}", program.getId())
                                .header("X-Authorization-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.programInfo.id").value(program.getId()))
                .andExpect(jsonPath("$.programInfo.title").value(program.getTitle()))
                .andExpect(jsonPath("$.programInfo.category").value(program.getCategory().getValue()))
                .andExpect(jsonPath("$.programInfo.maxMember").value(program.getMaxMember()))
                .andExpect(jsonPath("$.programInfo.goal").value(program.getGoal()))
                .andExpect(jsonPath("$.programInfo.description").value(program.getDescription()))
                .andExpect(jsonPath("$.programInfo.dueDate").value(program.getDueDate().toString()))
                .andExpect(jsonPath("$.programInfo.mentor.size()").value(0))
                .andExpect(jsonPath("$.isHost").value(true))
                .andDo(
                        document("/get/api-programs-programId",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("programId").description("Program pk want to get")
                                ), responseFields(
                                        fieldWithPath("programInfo.id").description("Program pk"),
                                        fieldWithPath("programInfo.title").description("Program title to create"),
                                        fieldWithPath("programInfo.goal").description("Program goal"),
                                        fieldWithPath("programInfo.description").description("Program description"),
                                        fieldWithPath("programInfo.maxMember").description("Maximum member can be able to participate program"),
                                        fieldWithPath("programInfo.category").description("Program's category"),
                                        fieldWithPath("programInfo.dueDate").description("due date of recruitment"),
                                        fieldWithPath("programInfo.mentor[]").description("List of mentor's info"),
                                        fieldWithPath("isHost").description("Whether member who request is host of the program or not")
                                )
                        ));
    }

    @Test
    @WithMockUser(username = "testA@email.com", password = "", roles = "USER")
    @DisplayName("프로그램_참가_신청_요청")
    void 프로그램_참가_신청_요청() throws Exception {

        Program program = (Program) data.get("programA");
        ApplyRequestDto requestForm
                = new ApplyRequestDto(3L, "testNick", program.getId(), "want to join", "MENTEE");

        String requestBody = objectMapper.writeValueAsString(requestForm);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/programs/{programId}/join", program.getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andDo(
                        document("/post/api-programs-programId-join",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("programId").description("Program pk want to get")
                                ),
                                requestFields(
                                        fieldWithPath("memberId").description("Login member id"),
                                        fieldWithPath("nickname").description("Login member nickname"),
                                        fieldWithPath("programId").description("Program pk want to join"),
                                        fieldWithPath("message").description("Left message to program's host"),
                                        fieldWithPath("role").description("Program role want to join as")
                                ),
                                responseFields(
                                        fieldWithPath("result").description("Result of request")
                                )
                        ));
    }

    @Test
    @WithMockUser(username = "testA@email.com", password = "", roles = "USER")
    @DisplayName("프로그램_신청자_관리_리스트_요청")
    void 프로그램_신청자_관리_리스트_요청() throws Exception {

        Program programA = (Program) data.get("programA");
        mockMvc.perform(
                        RestDocumentationRequestBuilders.get("/api/programs/{programId}/applicants", 1L)
                                .header("X-Authorization-Id", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.programInfo.title").value(programA.getTitle()))
                .andExpect(jsonPath("$.programInfo.id").value(programA.getId()))
                .andExpect(jsonPath("$.applicants.size()").value(1))
                .andExpect(jsonPath("$.applicants[0].nickname").value("testNickB"))
                .andExpect(jsonPath("$.applicants[0].programId").value(1))
                .andExpect(jsonPath("$.applicants[0].message").value("want to join"))
                .andExpect(jsonPath("$.curNum").value("1"))
                .andDo(
                        document("/get/api-programs-programId-applicants",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("programId").description("Program pk want to get")
                                ),
                                responseFields(
                                        fieldWithPath("programInfo").description("Program information"),
                                        fieldWithPath("programInfo.id").description("Program pk"),
                                        fieldWithPath("programInfo.title").description("Program title to create"),
                                        fieldWithPath("programInfo.goal").description("Program goal"),
                                        fieldWithPath("programInfo.description").description("Program description"),
                                        fieldWithPath("programInfo.maxMember").description("Maximum member can be able to participate program"),
                                        fieldWithPath("programInfo.category").description("Program's category"),
                                        fieldWithPath("programInfo.dueDate").description("due date of recruitment"),
                                        fieldWithPath("programInfo.mentor[]").description("List of mentor's info"),
                                        fieldWithPath("applicants").description("Applicants who applied to join in"),
                                        fieldWithPath("applicants[].memberId").description("Applicant's member pk"),
                                        fieldWithPath("applicants[].nickname").description("Applicant's nickname"),
                                        fieldWithPath("applicants[].programId").description("Applied program pk"),
                                        fieldWithPath("applicants[].message").description("Applicant's message left to host"),
                                        fieldWithPath("applicants[].role").description("Role which applicant wants to take part in"),
                                        fieldWithPath("curNum").description("Current number of member who joined")
                                )
                        ));
    }

    @Test
    @WithMockUser(username = "testA@email.com", password = "", roles = "USER")
    @DisplayName("참가자_승인_요청")
    void 참가자_승인_요청() throws Exception {

        Program programA = (Program) data.get("programA");
        ApplyRequestDto requestForm = new ApplyRequestDto(2L, "testNickB", programA.getId(), "want to join", "MENTEE");
        String requestBody = objectMapper.writeValueAsString(requestForm);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/programs/{programId}/applicants/accept", programA.getId())
                                .header("X-Authorization-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andDo(
                        document("/post/api-programs-programId-applicants-accept",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("programId").description("Program pk want to get")
                                ),
                                requestFields(
                                        fieldWithPath("memberId").description("Applicant member id"),
                                        fieldWithPath("nickname").description("Applicant member nickname"),
                                        fieldWithPath("programId").description("Program pk want to join"),
                                        fieldWithPath("message").description("Left message to program's host"),
                                        fieldWithPath("role").description("Program role want to join as")
                                ),
                                responseFields(
                                        fieldWithPath("result").description("Result of user request")
                                )
                        )
                );
    }

    @Test
    @WithMockUser(username = "testA@email.com", password = "", roles = "USER")
    @DisplayName("참가자_참가요청_거절")
    void 참가자_참가요청_거절() throws Exception {

        Program programA = (Program) data.get("programA");
        ApplyRequestDto requestForm = new ApplyRequestDto(2L, "testNickB", programA.getId(), "want to join", "MENTEE");
        String requestBody = objectMapper.writeValueAsString(requestForm);

        mockMvc.perform(
                        RestDocumentationRequestBuilders.post("/api/programs/{programId}/applicants/reject", programA.getId())
                                .header("X-Authorization-Id", "1")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody)
                                .with(csrf())
                ).andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"))
                .andDo(
                        document("/post/api-programs-programId-applicants-reject",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                pathParameters(
                                        parameterWithName("programId").description("Program pk want to get")
                                ),
                                requestFields(
                                        fieldWithPath("memberId").description("Applicant member id"),
                                        fieldWithPath("nickname").description("Applicant member nickname"),
                                        fieldWithPath("programId").description("Program pk want to join"),
                                        fieldWithPath("message").description("Left message to program's host"),
                                        fieldWithPath("role").description("Program role want to join as")
                                ),
                                responseFields(
                                        fieldWithPath("result").description("Result of user request")
                                )
                        )
                );
    }

}
