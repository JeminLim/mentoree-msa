package com.mentoree.common.kafka.dto;

import com.mentoree.common.domain.BaseTimeEntity;
import com.mentoree.common.kafka.utils.Field;
import com.mentoree.common.kafka.utils.Schema;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

import static com.mentoree.common.kafka.utils.Field.mapper;

@Getter
@Setter
@Component
public class SaveData {

    private List<Field> fields = new ArrayList<>();
    private Schema schema;

    public SaveData() {}

    public <T extends BaseTimeEntity> void setData(T entity) {
        this.fields = mapper(entity);
        this.schema = Schema.builder()
                .type("struct")
                .fields(fields)
                .optional(false)
                .name(entity.getClass().getSimpleName())
                .build();
    }

}
