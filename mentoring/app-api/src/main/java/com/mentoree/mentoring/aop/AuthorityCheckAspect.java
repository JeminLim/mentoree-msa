package com.mentoree.mentoring.aop;

import com.mentoree.common.advice.exception.NoAuthorityException;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.servlet.HandlerMapping;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Aspect
@Slf4j
@Component
@RequiredArgsConstructor
public class AuthorityCheckAspect {

    private final ParticipantRepository participantRepository;

    @Pointcut("within(com.mentoree.mentoring.api.controller.MissionApiController)")
    public void missionApi() {}

    @Pointcut("within(com.mentoree.mentoring.api.controller.BoardApiController)")
    public void boardApi() {}


    @Before("boardApi() || missionApi()")
    public void checkAuthority() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        long loginMemberId = Long.parseLong(request.getHeader("X-Authorization-Id"));
        List<Long> participantIdList = getParticipants(request).stream()
                                        .map(Participant::getMemberId)
                                        .collect(Collectors.toList());

        if(!participantIdList.isEmpty() && !participantIdList.contains(loginMemberId)) {
            throw new NoAuthorityException("프로그램 참가자가 아닙니다.");
        }

        return;
    }

    private List<Participant> getParticipants(HttpServletRequest request) {
        Map<?, ?> pathVariable = (Map<?, ?>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
        Map<String, String[]> queryParam = request.getParameterMap();

        if (queryParam.containsKey("programId")) {
            return participantRepository.findAllParticipantByProgramId(Long.parseLong(queryParam.get("programId")[0]));
        }

        if (pathVariable.containsKey("missionId")) {
            return participantRepository.findAllParticipantByMissionId(Long.parseLong((String) pathVariable.get("missionId")));
        }

        if (pathVariable.containsKey("boardId")) {
            return participantRepository.findAllParticipantByBoardId(Long.parseLong((String) pathVariable.get("boardId")));
        }

        return new ArrayList<>();
    }


}
