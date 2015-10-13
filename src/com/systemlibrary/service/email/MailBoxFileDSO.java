package com.systemlibrary.service.email;

public class MailBoxFileDSO 
{
	public String emailAddress;
	public String emailPrefix;
	public String emailDomain;
	
	public MailBoxFileDSO()
	{
		this.emailAddress = "";				//SOME@DOE.COM
		this.emailPrefix = "";				//SOME
		this.emailDomain = "";				//DOE.COM
	}
	
	/*
	 * This function will build the line to write to the file from the contents of this DSO.
	 * Ex: info@mydomain.com	mydomain.com/info/
	 * 
	 * @return	String
	 */
	public String generateContentToWriteToFile()
	{
		String line = String.format("%s\t%s/%s/", this.emailAddress, this.emailDomain, this.emailPrefix);
		return line;
	}

}
