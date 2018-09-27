package com.avro;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileStream;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.io.JsonEncoder;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.Bucket;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Test {
	
	public static void main(String[] args) throws IOException {
		 AmazonS3ClientBuilder s3BucketClientBuilder = AmazonS3ClientBuilder.standard();
	        s3BucketClientBuilder.setRegion("us-east-1");
	        s3BucketClientBuilder.setCredentials(new AWSStaticCredentialsProvider(
	                new BasicAWSCredentials("AKIAICDMQ4GAGWWGVZXQ", "29Hm/YIw1K3DQzENsF+NRdR6OlF0wueYjIiYPIAx")));

	         //Write to S3 Bucket 
	        AmazonS3 s3Bucket = s3BucketClientBuilder.build();
	      String str=null ;
	        List<Bucket> buckets = s3Bucket.listBuckets();
	        System.out.println("Your Amazon S3 buckets are:");
	        for (Bucket b : buckets) {
	            if(b.getName().equals("accent-analytics-vn-us-east-1")) {
	            	str = b.getName();
	            }
	        }
	        
	        //ObjectMetadata objectMetadata = s3Bucket.getObjectMetadata(str, "integration/success-ss-vn");
	        	/*	listObjects(new ListObjectsRequest()
	                .withBucketName(str).withPrefix("integration/success-ss-vn/accent-technologies.com/"));*/
	        S3Object s3object = s3Bucket.getObject((new GetObjectRequest(
	                str.toString(), "integration/sfdc/accent-technologies.com/sync/20180924080949/account.avro")));//integration/success-ss-vn\\exchtest7@accenttest.local/crm-tasks-2018-09-06T11:24:10.977Z.avro
	        
	        InputStream in = s3object.getObjectContent();
	        
	        String a = avroToJson(IOUtils.toByteArray(in));
	        System.out.println("AAAAAAAAAAAAA");
	        ObjectMapper mapper = new ObjectMapper();
	
	        try {  

	            // Writing to a file   
	            mapper.writeValue(new File("D:\\task1.json"), a );

	        } catch (IOException e) {  
	            e.printStackTrace();  
	        }  
	      
	       /*String[] stra = a.split("[^{\"message\"]");*/
	     
	  
	      System.out.println("FINISH");

	}
	
	

	private static ListObjectsV2Request createRequest(String bucketName, String path) {
	    ListObjectsV2Request request = new ListObjectsV2Request();
	    request.setPrefix(path);
	    request.withBucketName(bucketName);
	    return request;
	}
	
	public static String avroToJson(byte[] avro) throws IOException {
	    boolean pretty = false;
	    GenericDatumReader<GenericRecord> reader = null;
	    JsonEncoder encoder = null;
	    ByteArrayOutputStream output = null;
	    try {
	        reader = new GenericDatumReader<GenericRecord>();
	        InputStream input = new ByteArrayInputStream(avro);
	        DataFileStream<GenericRecord> streamReader = new DataFileStream<GenericRecord>(input, reader);
	        output = new ByteArrayOutputStream();
	        Schema schema = streamReader.getSchema();
	        System.out.println(schema.toString(true));
	        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema);
	        encoder = EncoderFactory.get().jsonEncoder(schema, output, pretty);
	        for (GenericRecord datum : streamReader) {
	            writer.write(datum, encoder);
	        }
	        encoder.flush();
	        output.flush();
	        return new String(output.toByteArray());
	    } finally {
	        try { if (output != null) output.close(); } catch (Exception e) { }
	    }
	}
 
	/*  public static String avroToJson(byte[] avro) throws IOException {
		    boolean pretty = false;
		    GenericDatumReader<GenericRecord> reader = null;
		    JsonEncoder encoder = null;
		    ByteArrayOutputStream output = null;
		    try {
		        reader = new GenericDatumReader<GenericRecord>();
		        InputStream input = new ByteArrayInputStream(avro);
		        DataFileStream<GenericRecord> streamReader = new DataFileStream<GenericRecord>(input, reader);
		        output = new ByteArrayOutputStream();
		        List<Schema> schema = streamReader.getSchema().getTypes();
		        DatumWriter<GenericRecord> writer = new GenericDatumWriter<GenericRecord>(schema.get(0));
		        encoder = EncoderFactory.get().jsonEncoder(schema.get(0), output, pretty);
		        for (GenericRecord datum : streamReader) {
		            writer.write(datum, encoder);
		        }
		        encoder.flush();
		        output.flush();
		        return new String(output.toByteArray());
		    } finally {
		        try { if (output != null) output.close(); } catch (Exception e) { }
		    }
		}*/
	
}
