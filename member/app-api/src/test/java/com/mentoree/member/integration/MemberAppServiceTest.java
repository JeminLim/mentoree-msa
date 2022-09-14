package com.mentoree.member.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.member.MemberApiApplication;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.dto.MemberInfo;
import com.mentoree.member.integration.DataPreparation;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.annotation.Rollback;
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
@ExtendWith(RestDocumentationExtension.class)
public class MemberAppServiceTest {

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
                                .param("email", testerA.getEmail())
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(testerA.getEmail()))
                .andExpect(jsonPath("$.memberName").value(testerA.getMemberName()))
                .andExpect(jsonPath("$.nickname").value(testerA.getNickname()))
                .andExpect(jsonPath("$.interests[0]")
                        .value(testerA.getInterest().get(0).getCategory().getValue()))
                .andDo(
                        document("/get/api/members/profile",
                                preprocessRequest(prettyPrint()),
                                preprocessResponse(prettyPrint()),
                                requestParameters(
                                       parameterWithName("email").description("user's email which wants to get")
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
                .memberName(testerA.getMemberName())
                .email(testerA.getEmail())
                .nickname("changedNickname")
                .interests(Arrays.asList("LIFE", "MUSIC", "EMPLOYMENT"))
                .link("this is testerA self description")
                .build();
        String requestBody = objectMapper.writeValueAsString(update);

        mockMvc.perform(
                post("/api/members/profile")
                        .header("X-Authorization-User", "testA@email.com")
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
                                fieldWithPath("result").description("Result of request - success or failed")
                        )
                ));
    }



}
