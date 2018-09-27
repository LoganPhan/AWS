package com.accent.ees.publisher.aws;

import java.util.LinkedHashMap;
import java.util.List;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PublisherCrmRequest <PayloadType> {

    private List<PayloadType> payload;

    private AwsConfig awsConfig;

    private String client;
    
    private String crmObjectName;
    
    private LinkedHashMap<String, Object> objectSchema;
}
