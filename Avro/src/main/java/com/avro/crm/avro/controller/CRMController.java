package com.avro.crm.avro.controller;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.Instant;
import java.util.LinkedHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.amazonaws.AmazonServiceException;
import com.avro.Utils.Utils;
import com.avro.aws.AwsS3Facade;
import com.avro.aws.CrmFolder;
import com.avro.aws.PublisherCrmRequest;
import com.avro.aws.PublisherCrmResponse;
import com.avro.crm.avro.AvroAdapter;

import lombok.extern.slf4j.Slf4j;

/**
 * This class was created to isolate the CRM related Publish end-points from the Exchange related data endpoints.
 * <p>
 * TODO - Moving forward, this design pattern should be use to convert AVRO files from the given requests and the
 * current publisher controller should be refactored to meet this design pattern.
 */
@Slf4j
@RestController
@RequestMapping(value = "/v1/crm", consumes = {"application/json"}, produces = {"application/json"})
public class CRMController {

    /** Adapter for Creating Avro Data Streams */
    private final AvroAdapter avroAdapter;

    /** Facade for Saving Data to AWS */
    private final AwsS3Facade awsFacade;

    @Autowired
    public CRMController(AvroAdapter avroAdapter, AwsS3Facade awsFacade) {

        this.avroAdapter = avroAdapter;
        this.awsFacade = awsFacade;
    }
    
    @RequestMapping(value = "/salesforce", method = RequestMethod.POST)
	public PublisherCrmResponse publishedCrmObject(
			@RequestBody PublisherCrmRequest<LinkedHashMap<String, Object>> request) throws Exception {
		log.info("Received Publish Request - CRM {}: {}", request.getCrmObjectName(), request.getClient());
		
		/* Capture Number of Accounts */
		int numberOfRecords = request.getPayload().size();
		
		try {
			/* Create Avro File OutputStream and Convert into InputStream */
			ByteArrayInputStream fileInputStream = avroAdapter
					.createAvroByteArrayInputStream(request.getObjectSchema(), request.getPayload(), request.getCrmObjectName())
					.orElseThrow(() -> new IOException("Unable to retrieve Avro Output Stream"));
			
			/* Write to File on S3 */
			String awsBucketPath = Utils.buildBucketPath(Utils.SF_CRM_PATH, 
					request.getClient(),
					Utils.AWS_CRM_DATE_FORMAT, 
					Instant.now(), 
					CrmFolder.valueOf(request.getCrmObjectName()).value + ".avro");
			
			awsFacade.writeFileToS3(request.getAwsConfig(), fileInputStream, awsBucketPath);

			log.debug("File written to S3 Bucket- CRM Contacts: {}", request.getClient());
		} catch (AmazonServiceException e) {
			log.error("Unable to write to S3 Bucket", e);
			numberOfRecords = 0;
		}

		return new PublisherCrmResponse()
				.setClient(request.getClient())
				.setRecordsPublished(numberOfRecords)
				.setCrmType("Type")
				.setBucketName(request.getAwsConfig().getBucketName());
    }
}