package com.mentoree.mentoring.messagequeue.connect;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mentoree.common.domain.BaseTimeEntity;
import com.mentoree.kafka.utils.Field;
import com.mentoree.kafka.utils.Schema;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.mentoree.kafka.utils.Field.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectProducer<T extends BaseTimeEntity> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private List<Field> fields;
    private Schema schema;

    public void setSchemaAndFields(T entity) {
        this.fields = mapper(entity);
        this.schema = Schema.builder()
                .type("struct")
                .fields(fields)
                .optional(false)
                .name(entity.getClass().getSimpleName())
                .build();
    }

    public T send(String topic, T data) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = "";
        try {
            jsonString = mapper.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        kafkaTemplate.send(topic, jsonString);
        log.info("Program producer sent data from programService: {}", data);

        return data;
    }


}
