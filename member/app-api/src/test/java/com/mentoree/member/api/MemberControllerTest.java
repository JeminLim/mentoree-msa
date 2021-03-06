package com.mentoree.member.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.member.api.controller.MemberProfileApiController;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.UserRole;
import com.mentoree.member.dto.MemberInfo;
import com.mentoree.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest
public class MemberControllerTest {

    @MockBean
    MemberService memberService;

    @Autowired
    ObjectMapper objectMapper;

    @Autowired
    MockMvc mockMvc;

    Member testMember;

    @BeforeEach
    public void setUp() {

        testMember = Member.builder()
                .memberName("tester")
                .email("test@email.com")
                .link("link")
                .nickname("testNickname")
                .oAuth2Id("google")
                .role(UserRole.USER)
                .build();

    }


    @Test
    @WithMockUser
    @DisplayName("??????_?????????_????????????_?????????")
    void getMemberProfileTest() throws Exception {
        //given
        MemberInfo memberInfo = MemberInfo.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("testNick")
                .link("link")
                .build();
        when(memberService.getMemberProfile(any(String.class))).thenReturn(memberInfo);
        //when
        ResultActions result = mockMvc.perform(
                get("/api/members/profile")
                        .param("email", "test@email.com")
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("test@email.com"))
                .andExpect(jsonPath("$.memberName").value("tester"))
                .andExpect(jsonPath("$.nickname").value("testNick"))
                .andExpect(jsonPath("$.link").value("link"))
                .andDo(print());
    }

    @Test
    @WithMockUser
    @DisplayName("??????_?????????_??????_?????????")
    void ??????_?????????_??????_?????????() throws Exception {
        //given
        MemberInfo memberInfo = MemberInfo.builder()
                .email("test@email.com")
                .memberName("tester")
                .nickname("changedNick")
                .link("new link")
                .build();

        String requestBody = objectMapper.writeValueAsString(memberInfo);

        //when
        ResultActions result = mockMvc.perform(
                post("/api/members/profile")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                        .with(csrf())
        );
        //then
        result.andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value("success"));
    }

}
