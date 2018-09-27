package com.accent.ees.publisher;

import java.util.Arrays;

import org.dozer.DozerBeanMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan
@EnableAutoConfiguration
public class ExchangeDatapublisherApplication {

    public static void main(String[] args) {

        SpringApplication.run(ExchangeDatapublisherApplication.class, args);
    }

    @Bean(name = "org.dozer.Mapper")
    public DozerBeanMapper dozerBean() {

        DozerBeanMapper dozerBean = new DozerBeanMapper();
        dozerBean.setMappingFiles(Arrays.asList(
                "dozer-account-mappings.xml",
                "dozer-contact-mappings.xml",
                "dozer-opportunity-mappings.xml",
                "dozer-event-mappings.xml",
                "dozer-lead-mappings.xml",
                "dozer-task-mappings.xml",
                "dozer-user-mappings.xml"));

        return dozerBean;
    }

}