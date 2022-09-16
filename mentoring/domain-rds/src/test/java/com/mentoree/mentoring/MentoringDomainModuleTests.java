package com.mentoree.mentoring;

import com.mentoree.mentoring.common.DataPreparation;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class MentoringDomainModuleTests {

    public void contextLoads() {}

    @Bean
    public DataPreparation dataPreparation() {
        return new DataPreparation();
    }

}
