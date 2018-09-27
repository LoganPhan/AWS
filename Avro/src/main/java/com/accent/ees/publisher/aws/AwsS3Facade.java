package com.accent.ees.publisher.aws;

import java.io.InputStream;

import org.springframework.stereotype.Service;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.PutObjectRequest;

@Service
public class AwsS3Facade {

    public void writeFileToS3(AwsConfig awsConfig, InputStream inputStream, String filename) {

        /* Setup AWS Credentials Config */
        AmazonS3ClientBuilder s3BucketClientBuilder = AmazonS3ClientBuilder.standard();
        s3BucketClientBuilder.setRegion(awsConfig.getRegion());
        s3BucketClientBuilder.setCredentials(new AWSStaticCredentialsProvider(
                new BasicAWSCredentials(awsConfig.getAccessKey(), awsConfig.getSecretKey())));

        /* Write to S3 Bucket */
        AmazonS3 s3Bucket = s3BucketClientBuilder.build();
		s3Bucket.putObject(new PutObjectRequest(awsConfig.getBucketName(), awsConfig.getBucketPath() + filename,
				inputStream, null));
    }
    
}
