package com.mentoree.member.service;

import com.mentoree.common.domain.Category;
import com.mentoree.common.dto.messagequeue.UpdateMember;
import com.mentoree.common.interenal.ResponseMember;
import com.mentoree.member.domain.entity.MemberInterest;
import com.mentoree.member.domain.repository.MemberInterestRepository;
import com.mentoree.member.dto.MemberInfo;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.repository.MemberRepository;
import com.mentoree.member.messagequeue.producer.KafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;
    private final MemberInterestRepository memberInterestRepository;
    private final KafkaProducer kafkaProducer;


    @Transactional(readOnly = true)
    public MemberInfo getMemberProfile(Long id) {
        Member findMember = memberRepository.findMemberById(id)
                .orElseThrow(() -> new NoSuchElementException("유저를 못찾았슴다"));

        return MemberInfo.of(findMember);
    }

    @Transactional(readOnly = true)
    public ResponseMember getMemberInfo(Long memberId) {
        Optional<Member> member = memberRepository.findMemberById(memberId);

        if(member.isEmpty())
            throw new NoSuchElementException("회원이 존재하지 않습니다.");

        Member targetMember = member.get();
        List<String> interest = targetMember.getInterest().stream().map(i -> i.getCategory().getValue()).collect(Collectors.toList());

        return new ResponseMember(targetMember.getId(), targetMember.getNickname(), interest);
    }

    @Transactional
    public MemberInfo updateMemberProfile(MemberInfo memberInfo) {
        Member member = memberRepository.findMemberByEmail(memberInfo.getEmail())
                .orElseThrow(() -> new NoSuchElementException("유저 없음"));

        /** Kafka 닉네임 업데이트시 메시지 전송 */
        if(!member.getNickname().equals(memberInfo.getNickname())) {
            kafkaProducer.send("member-profile-update-topic",
                    UpdateMember.builder().memberId(member.getId()).nickname(memberInfo.getNickname()).build());
            member.updateNickname(memberInfo.getNickname());
        }

        member.updateLink(memberInfo.getLink());

        /**
         * A collection with cascade="all-delete-orphan" was no longer referenced by the owning entity instance 오류
         */
        List<MemberInterest> interestList = memberInfo.getInterests().stream().map(
                        s -> MemberInterest.builder().member(member).category(Category.valueOf(s)).build())
                .collect(Collectors.toList());
        List<MemberInterest> result = memberInterestRepository.saveAll(interestList);
        member.addInterest(result);


        MemberInfo updatedInfo = MemberInfo.of(memberRepository.save(member));
        return updatedInfo;
    }



}
