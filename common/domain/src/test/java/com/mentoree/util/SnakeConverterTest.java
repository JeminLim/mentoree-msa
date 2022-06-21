package com.mentoree.util;

import com.mentoree.common.domain.SnakeConverter;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class SnakeConverterTest {

    @Test
    public void Snake_Converter_Test_Success() {
        String camelCase = "MentoreeApplication";
        String snakeCase = SnakeConverter.convertToSnakeCase(camelCase);
        System.out.println("camel case : " + camelCase);
        System.out.println("snake case : " + snakeCase);
        Assertions.assertThat(snakeCase).isEqualTo("mentoree_application");
    }

}
