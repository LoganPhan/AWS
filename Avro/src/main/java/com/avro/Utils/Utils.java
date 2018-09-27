package com.avro.Utils;

import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * 
 * @author longphan
 *
 */
public class Utils {
	
	public static final String SF_CRM_PATH = "sfdc";
	public static final String SUGAR_CRM_PATH = "sugar";
    public static final String AVRO_DATE_FORMAT = "yyyy-MM-dd hh:mm:ss";
    public static final String AWS_CRM_DATE_FORMAT = "yyyyMMddHHmmss";
    
	/**
	 * Convert Instant to String on specific pattern
	 * @param pattern
	 * @param date
	 * @return
	 */
	public static String covertToDateString(final String pattern, final Instant date) {
	   if (date == null) {
            return null;
        }
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern).withZone(ZoneOffset.UTC);
        return formatter.format(date);
	}
	
	/**
	 * The naming convention for the files stored in S3</br>
	 * {bucketName}/integration/{crmName}/{envName}/sync/{yyyyMMddHHmmss}/{objectName}.avro
	 * @param crmName
	 * @param envName
	 * @param pattern
	 * @param file
	 * @return
	 */
	public static String buildBucketPath(String crmName, String envName, String pattern, Instant date, String file) {
		return String.format("/%s/%s/sync/%s%s", crmName, envName, covertToDateString(pattern, date), file);

	}
}
