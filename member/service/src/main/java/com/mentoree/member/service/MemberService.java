package com.mentoree.member.service;

import com.mentoree.member.dto.MemberInfo;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberInfo getMemberProfile(String email) {
        Member findMember = memberRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("유저를 못찾았슴다"));
        List<String> interestList = findMember.getInterest().stream()
                                    .map(mi -> mi.getCategory().getValue())
                                    .collect(Collectors.toList());

        return MemberInfo.builder()
                .memberName(findMember.getMemberName())
                .link(findMember.getLink())
                .email(findMember.getEmail())
                .nickname(findMember.getNickname())
                .interests(interestList)
                .participatedProgramIdList(findMember.getParticipatedPrograms())
                .build();
    }

    @Transactional
    public void updateMemberProfile(MemberInfo memberInfo) {
        Member member = memberRepository.findByEmail(memberInfo.getEmail())
                .orElseThrow(() -> new NoSuchElementException("유저 없음"));

        member.updateNickname(memberInfo.getNickname());
        member.updateLink(memberInfo.getLink());
        member.updateInterest(memberInfo.getInterests());
    }

}
