package com.mentoree.mentoring.messagequeue.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.common.domain.BaseTimeEntity;
import com.mentoree.common.kafka.dto.SaveData;
import com.mentoree.common.kafka.utils.Field;
import com.mentoree.common.kafka.utils.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectProducer<T extends BaseTimeEntity> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final SaveData saveData;

    public T send(String topic, T data) {

        System.out.println("fu");
        saveData.setData(data);

        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(saveData);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonString);
        log.info("Program producer sent data from programService: {}", data);

        return data;
    }


}
