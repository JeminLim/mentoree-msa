package com.mentoree.member.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.repository.MemberInterestRepository;
import com.mentoree.member.domain.repository.MemberRepository;
import com.mentoree.member.dto.MemberInfo;
import com.netflix.discovery.converters.Auto;
import org.apache.kafka.clients.producer.MockProducer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureRestDocs
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092"})
@ExtendWith(RestDocumentationExtension.class)
public class MemberAppIntegrationTest {

    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;
    @Autowired
    DataPreparation dataPreparation;

    Map<String, Object> data = new HashMap<>();
    @BeforeEach
    void setUp() {
        data = dataPreparation.getData();
    }

    @Test
    @WithMockUser(username="testA@email.com", password = "", roles = "USER")
    @DisplayName("프로필_요청")
    @Transactional
    void 프로필_요청() throws Exception {

        Member testerA = (Member) data.get("memberA");
        mockMvc.perform(
                        get("/api/members/profile")
                                .header("X-Authorization-Id", testerA.getId())
                                .param("memberId", testerA.getId().toString())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testerA.getEmail()))
                .andExpect(jsonPath("$.memberName").value(testerA.getMemberName()))
                .andExpect(jsonPath("$.nickname").value(testerA.getNickname()))
                .andExpect(jsonPath("$.interests[0]")
                        .value(testerA.getInterest().get(0).getCategory().getKey()))
                .andDo(
                        document("/get/api/members/profile",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                       parameterWithName("memberId").description("Target user id")
                                ),
                                responseFields(
                                        fieldWithPath("memberId").description("The user's id"),
                                        fieldWithPath("email").description("The user's email address"),
                                        fieldWithPath("memberName").description("The user's name"),
                                        fieldWithPath("nickname").description("The user's nickname"),
                                        fieldWithPath("interests").description("An array of the user's interested category"),
                                        fieldWithPath("link").description("Self-description with user's career")
                                )
                        ));
    }

    @Test
    @WithMockUser(username = "testA@email.com", password="", roles = "USER")
    @DisplayName("프로필_수정_요청")
    @Transactional
    void 프로필_수정_요청() throws Exception {
        Member testerA = (Member) data.get("memberA");
        MemberInfo update = MemberInfo.builder()
                .memberId(testerA.getId())
                .memberName(testerA.getMemberName())
                .email(testerA.getEmail())
                .nickname("changedNickname")
                .interests(Arrays.asList("LIFE", "MUSIC", "EMPLOYMENT"))
                .link("this is testerA self description")
                .build();
        String requestBody = objectMapper.writeValueAsString(update);

        mockMvc.perform(
                post("/api/members/profile")
                        .header("X-Authorization-Id", testerA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf())
        ).andExpect(status().isOk())
        .andExpect(jsonPath("$.result").value("success"))
        .andDo(
                document("/post/api/members/profile",
                        preprocessRequest(prettyPrint()),
                        preprocessResponse(prettyPrint()),
                        requestFields(
                                fieldWithPath("memberId").description("The user's id"),
                                fieldWithPath("email").description("The user's email address(Immutable)"),
                                fieldWithPath("memberName").description("The user's name(Immutable)"),
                                fieldWithPath("nickname").description("Changed user nickname"),
                                fieldWithPath("interests").description("An array of changed category the user's interested in"),
                                fieldWithPath("link").description("Changed self-description with user's career")
                        ),
                        responseFields(
                                fieldWithPath("result").description("Result of request - success or failed"),
                                fieldWithPath("memberInfo.memberId").description("Updated user id"),
                                fieldWithPath("memberInfo.email").description("Updated user email"),
                                fieldWithPath("memberInfo.memberName").description("Updated user member's name"),
                                fieldWithPath("memberInfo.nickname").description("Updated user nickname"),
                                fieldWithPath("memberInfo.interests").description("Updated user interests"),
                                fieldWithPath("memberInfo.link").description("Updated user self-description")
                        )
                ));
    }



}
