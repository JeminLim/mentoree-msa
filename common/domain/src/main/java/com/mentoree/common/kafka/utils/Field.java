package com.mentoree.common.kafka.utils;

import com.mentoree.common.domain.BaseTimeEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@AllArgsConstructor
public class Field {

    private String type;
    private boolean optional;
    private String field;

    public static <T extends BaseTimeEntity> List<Field> mapper(T entity) {
        return Arrays.stream(entity.getClass().getDeclaredFields()).map(field
                        -> new Field(DataBaseType.valueOf(field.getDeclaringClass().getSimpleName()).toString(),
                        Arrays.stream(field.getAnnotations())
                                .anyMatch(annotation -> annotation.getClass().getSimpleName().equals("NotNull")),
                        SnakeConverter.convertToSnakeCase(field.getName())))
                .collect(Collectors.toList());
    }

}
