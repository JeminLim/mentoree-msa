package com.mentoree.member.messagequeue;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.member.domain.entity.Member;
import com.mentoree.member.domain.entity.ParticipatedProgram;
import com.mentoree.member.domain.repository.MemberRepository;
import com.mentoree.member.domain.repository.ParticipatedProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
@Slf4j
@RequiredArgsConstructor
public class kafkaConsumer {

    private final MemberRepository memberRepository;
    private final ParticipatedProgramRepository participatedProgramRepository;

    @KafkaListener(topics = "topic_name")
    public void participatedProgramUpdate(String kafkaMessage) {

        log.info("Kafka message: -> {}", kafkaMessage);

        Map<Object, Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try {
            map = mapper.readValue(kafkaMessage, new TypeReference<Map<Object, Object>>() { });
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        /** participated program update */
        Member member = memberRepository.findById((Long) map.get("memberId"))
                .orElseThrow(NoSuchElementException::new);
        ParticipatedProgram program = ParticipatedProgram.builder()
                .member(member)
                .programTitle((String) map.get("programTitle"))
                .build();
        participatedProgramRepository.save(program);
        member.participated(program);

    }
}
