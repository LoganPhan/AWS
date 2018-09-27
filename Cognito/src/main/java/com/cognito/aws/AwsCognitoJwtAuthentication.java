/**
 * 
 */
package com.cognito.aws;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.cognito.aws.model.CognitoUserPool;
import com.cognito.common.TasksConfigContants;
import com.cognito.exceptions.InvalidRequestException;

import lombok.extern.slf4j.Slf4j;

/**
 * @author longphan
 *
 */
@Component
@Slf4j
public class AwsCognitoJwtAuthentication {

	@Autowired
	private AwsCognitoRSAKeyProvider cognitoRsaKeyProvider;
	
	/**
	 * Verify idToken from Cognito user pools
	 * @author longphan
	 * @return
	 * @throws com.cognito.exceptions.InvalidRequestException 
	 */
	public boolean verifyIdToken(@NotNull @Valid CognitoUserPool userPools) throws InvalidRequestException {
		try {
			cognitoRsaKeyProvider.buildAwsKidStoreUrl(userPools.getRegion(), userPools.getUserPoolId());
	    	Algorithm algorithm = Algorithm.RSA256(cognitoRsaKeyProvider);
	    	JWTVerifier jwtVerifier = JWT.require(algorithm)
	    	    .withAudience(userPools.getClientId()) // Validate your apps audience if needed
	    	    .build();

	    	DecodedJWT claimsSet = jwtVerifier.verify(userPools.getIdToken());
	    	
	    	if(!isIdToken(claimsSet)) {
	    		String msg = "JWT Token doesn't seem to be an ID Token";
	    		log.warn(msg);
	    		throw new InvalidRequestException(msg);
	    	}
	    	if(!claimsSet.getClaim(TasksConfigContants.COGNITO_USERNAME).asString().equals(userPools.getUsername())) {
	    		String msg = "JWT Token doesn't seem to be belong to this user: %s";
	    		log.warn(msg);
	    		throw new InvalidRequestException(String.format(msg, userPools.getUsername()));
	    	}
	    	return true;
		} catch (Exception e) {
    		log.warn(e.getMessage());
			throw new InvalidRequestException(e.getMessage());
		}
	}

	/**
	 * Check the Token which receive from client is Cognito's idToken
	 * @author longphan
	 * @param claimsSet
	 * @return
	 */
    private boolean isIdToken(DecodedJWT claimsSet) {
        return claimsSet.getClaim(TasksConfigContants.COGNITO_TOKEN_USE).asString().equals(TasksConfigContants.ID);
    }
    
}
