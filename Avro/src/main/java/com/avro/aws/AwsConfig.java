package com.avro.aws;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class AwsConfig {

    @JsonProperty("AWS_ACCESS_KEY")
    private String accessKey;

    @JsonProperty("AWS_S3_BUCKET_NAME")
    private String bucketName;

    @JsonProperty("AWS_S3_BUCKET_PATH")
    private String bucketPath;

    private Integer id;

    private String name;

    @JsonProperty("AWS_REGION")
    private String region;

    @JsonProperty("AWS_SECRET_KEY")
    private String secretKey;

    private String streamName;

    private String configApiKey;

}
