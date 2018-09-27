package com.avro.aws;

/**
 * 
 * @author longphan
 *
 */
public enum CrmFolder {

	Account("/account"),
	Event("/event"),
	Lead("/lead"),
	Opportunity("/opportunity"),
	Task("/task"),
	User("/user"),
	Contact("/contact"),
	OpportunityContactRole("/opportunity-contact-role");
	
	public String value;
	
	private CrmFolder(String value){
		this.value = value;
	}
	
}
