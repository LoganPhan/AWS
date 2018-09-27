package com.cognito.aws;

import java.net.MalformedURLException;
import java.net.URL;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import javax.xml.ws.WebServiceException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.amazonaws.services.simplesystemsmanagement.model.InvalidKeyIdException;
import com.auth0.jwk.Jwk;
import com.auth0.jwk.JwkProvider;
import com.auth0.jwk.JwkProviderBuilder;
import com.auth0.jwt.interfaces.RSAKeyProvider;

/**
 * @author longphan
 * 
 */
@Component
public class AwsCognitoRSAKeyProvider implements RSAKeyProvider {
	
	@Value("${aws.cognito-idp.jwks.url}")
	private String cognitoIdpJwksUrl;
	
	private URL awsKidStoreUrl;

	@Override
	public RSAPrivateKey getPrivateKey() {
		return null;
	}

	@Override
	public String getPrivateKeyId() {
		return null;
	}

	@Override
	public RSAPublicKey getPublicKeyById(String kid) {
		try {
			JwkProvider provider = new JwkProviderBuilder(awsKidStoreUrl).build();
			Jwk jwk = provider.get(kid);
			return (RSAPublicKey) jwk.getPublicKey();
		} catch (Exception e) {
			throw new InvalidKeyIdException(String.format("Failed to get JWT kid=%s from awsKidStoreUrl=%s", kid, awsKidStoreUrl));
		}
	}
	
	/**
	 * Initialize Amazon's Kid Store 
	 * @author longphan
	 * @param awsCognitoRegion
	 * @param awsUserPoolsId
	 */
	public void buildAwsKidStoreUrl(String awsCognitoRegion, String awsUserPoolsId) {
		String url = String.format(cognitoIdpJwksUrl, awsCognitoRegion, awsUserPoolsId);
		try {
			awsKidStoreUrl = new URL(url);
		} catch (MalformedURLException e) {
			throw new WebServiceException(String.format("Invalid URL provided, URL=%s", url));
		}
	}
}
