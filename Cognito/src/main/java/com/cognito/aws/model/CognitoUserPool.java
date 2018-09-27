/**
 * This Class has been created to store Cognito User Pool information.
 */
package com.cognito.aws.model;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author longphan
 *
 */
@Data
@Accessors(chain = true)
public class CognitoUserPool {
	private String username;
	private String userPoolId;
	private String clientId;
	private String region;
	private String idToken;
	
}
