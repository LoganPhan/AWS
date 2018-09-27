package com.avro.aws;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class PublisherCrmResponse {

    private String bucketName;

    private String client;

    private String crmType;

    private Integer recordsPublished;
}
