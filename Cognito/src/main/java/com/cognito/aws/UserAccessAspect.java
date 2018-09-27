package com.cognito.aws;

import javax.servlet.http.HttpServletRequest;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.cognito.aws.model.CognitoUserPool;
import com.cognito.common.Headers;
import com.cognito.exceptions.InvalidRequestException;

/**
 * @author longphan
 * 
 */
@Aspect
@Component
public class UserAccessAspect {
	
	@Autowired
	private AwsCognitoJwtAuthentication cognitoAuthen;
	
	@Around("@annotation(com.accent.connectors.taskconfig.annotations.IsAuthenticated)")
	public boolean verifyToken(JoinPoint joinPoint) throws InvalidRequestException{
	        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder
	                .currentRequestAttributes())
	                .getRequest();	
	        CognitoUserPool userpool = getCognitoUserPool(request);
	        try {
	        	return cognitoAuthen.verifyIdToken(userpool);
			} catch (Exception e) {
				throw new InvalidRequestException(e.getMessage());
			}
	}
	
	/**
	 * Verify and mapping header parameters
	 * @author longphan
	 * @param request
	 * @return CognitoUserPool
	 * @throws InvalidRequestException
	 */
	private CognitoUserPool getCognitoUserPool(HttpServletRequest request) throws InvalidRequestException {
		try {
			if (!Headers.isMissingHeaderParams(request)) 
				return new CognitoUserPool()
						.setClientId(request.getHeader(Headers.CLIENT_ID.getValue()))
						.setUsername(request.getHeader(Headers.USER_NAME.getValue()))
						.setRegion(request.getHeader(Headers.REGION.getValue()))
						.setUserPoolId(request.getHeader(Headers.USER_POOL_ID.getValue()))
						.setIdToken(request.getHeader(Headers.AUTHORIZATION.getValue()));
		} catch (InvalidRequestException e) {
			throw new InvalidRequestException(e.getMessage());
		}
		return null;
	}

}
