package com.cognito.common;

import javax.servlet.http.HttpServletRequest;

import com.cognito.exceptions.InvalidRequestException;

import lombok.extern.slf4j.Slf4j;

/**
 * Enum class
 * @author longphan
 *
 */
@Slf4j
public enum Headers {
	CLIENT_ID("Client-Id"),
	USER_NAME("User-Name"),
	USER_POOL_ID("User-Pool-Id"),
	REGION("Region"),
	AUTHORIZATION("Authorization");
	
	private String value;
	
	Headers(String value) {
        this.value = value;
    }
	
	/**
	 * This function determine which header param is missing from the request
	 * @author longphan
	 * @param request
	 * @return
	 * @throws InvalidRequestException
	 */
	public static boolean isMissingHeaderParams(HttpServletRequest request) throws InvalidRequestException {
		for (Headers header : Headers.values()) {
			String headerVal = request.getHeader(header.getValue());
			if(headerVal == null || headerVal.isEmpty()){
				String msg = "Missing|Empty request header '%s' for method parameter of type String";
				log.warn(msg);
				throw new InvalidRequestException(String.format(msg, header.getValue()));
			}
		}
		return false;
	}
	
	public String getValue() {
        return this.value;
    }
}
