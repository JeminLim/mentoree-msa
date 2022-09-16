package com.mentoree.mentoring.messagequeue.consumer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.common.dto.messagequeue.UpdateMember;
import com.mentoree.mentoring.domain.entity.Participant;
import com.mentoree.mentoring.domain.repository.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final ParticipantRepository participantRepository;

    @KafkaListener(topics = "member-profile-update-topic")
    public void updateNickname(String kafkaMessage) {

        log.info("Kafka message: {}", kafkaMessage);
        ObjectMapper objectMapper = new ObjectMapper();
        HashMap<String, Object> valueMap = new HashMap<>();
        try {
            JSONParser parser = new JSONParser();
            JSONObject parsingResult = (JSONObject) parser.parse(kafkaMessage);
            valueMap = objectMapper.readValue(parsingResult.toJSONString(), HashMap.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        // Integer 로 인식해서 자동 타입캐스팅이 되었다
        Long memberId = Long.parseLong(((Integer)valueMap.get("memberId")).toString());
        List<Participant> participantList = participantRepository.findAllParticipantByMemberId(memberId);
        String updatedNickname = (String)valueMap.get("nickname");
        participantList.forEach(p -> p.updateParticipantNickname(updatedNickname));
        participantRepository.saveAll(participantList);
    }


}
