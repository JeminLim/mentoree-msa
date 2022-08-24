package com.mentoree.member.messagequeue.producer;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.common.dto.messagequeue.UpdateMember;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public UpdateMember send(String topic, UpdateMember updateMemberDto) {

        ObjectMapper objectMapper = new ObjectMapper();

        String jsonString = "";
        try{
            jsonString = objectMapper.writeValueAsString(updateMemberDto);
        }catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonString);
        log.info("Kafka Producer sent data : {}", updateMemberDto);

        return updateMemberDto;
    }
}
