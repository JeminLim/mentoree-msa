package com.mentoree.common.kafka.utils;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class Schema {

    /**
     * type - name of table which want to save
     * fields - column spec for table
     * optional - optional
     * name - topic name
     * */
    private String type;
    private List<Field> fields;
    private boolean optional;
    private String name;

}
